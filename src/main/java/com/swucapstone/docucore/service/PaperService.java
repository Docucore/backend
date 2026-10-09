package com.swucapstone.docucore.service;

import com.swucapstone.docucore.dto.PaperDetailDto;
import com.swucapstone.docucore.dto.PaperListDto;
import com.swucapstone.docucore.dto.PaperReadDto;
import com.swucapstone.docucore.dto.SavedPaperDto;
import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Progress;
import com.swucapstone.docucore.entity.Save;
import com.swucapstone.docucore.entity.User;
import com.swucapstone.docucore.exception.UserNotFoundException;
import com.swucapstone.docucore.repository.PaperRepository;
import com.swucapstone.docucore.repository.ProgressRepository;
import com.swucapstone.docucore.repository.SaveRepository;
import com.swucapstone.docucore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperService {
    private final PaperRepository paperRepository;
    private final SaveRepository saveRepository;
    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;

    public List<PaperListDto> search(String keyword) {
        List<Paper> papers = paperRepository.findByTitleOrderByViewsDesc(keyword);
        List<PaperListDto> paperListDtos = new ArrayList<>();
        for (Paper paper : papers) {
            paperListDtos.add(PaperListDto.builder()
                    .title(paper.getTitle())
                    .id(paper.getId())
                    .author(paper.getAuthor())
                    .build());
        }
        return paperListDtos;
    }

    public PaperDetailDto findById(Long id) {
        Paper paper = paperRepository.findById(id).orElse(null);
        return PaperDetailDto.builder()
                .title(paper.getTitle())
                .build();
    }

    public PaperReadDto read(Long id) {
        Paper paper = paperRepository.findById(id).orElse(null);
        return PaperReadDto.builder().build();
    }

    public boolean saveById(Long paperId, Long userId) {
        Paper paper = paperRepository.findById(paperId).orElse(null);
        if (paper == null) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        Save save = saveRepository.findByPaperAndUser(paper, user).orElse(null);
        if (save == null) {
            save = Save.builder()
                    .user(user)
                    .paper(paper)
                    .build();
            saveRepository.save(save);
            return true;
        } else {
            saveRepository.delete(save);
        }
        return false;
    }


    ///  마이페이지 불러오기  ///
    public List<SavedPaperDto> savedPapers(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<Save> papers = saveRepository.findByUser(user);
        List<SavedPaperDto> savedPaperDtos = new ArrayList<>();
        if (papers.isEmpty()) {
            return savedPaperDtos;
        }
        for (Save save : papers) {
            SavedPaperDto dto = SavedPaperDto.builder()
                    .title(save.getPaper().getTitle())
                    .build();
            savedPaperDtos.add(dto);
        }
        return savedPaperDtos;
    }

    public List<SavedPaperDto> readingPapers(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<Progress> papers = progressRepository.findByUser(user);
        List<SavedPaperDto> savedPaperDtos = new ArrayList<>();
        if (papers.isEmpty()) {
            return savedPaperDtos;
        }
        for (Progress save : papers) {
            SavedPaperDto dto = SavedPaperDto.builder()
                    .title(save.getPaper().getTitle())
                    .build();
            savedPaperDtos.add(dto);
        }
        return savedPaperDtos;
    }

}
