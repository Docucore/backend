package com.swucapstone.docucore.service;

import com.swucapstone.docucore.dto.*;
import com.swucapstone.docucore.entity.*;
import com.swucapstone.docucore.exception.PaperNotFoundException;
import com.swucapstone.docucore.exception.UserNotFoundException;
import com.swucapstone.docucore.repository.*;
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
    private final SummaryRepository summaryRepository;
    private final SectionRepository sectionRepository;

    public List<PaperListDto> search(String keyword) {
        List<Paper> papers = paperRepository.searchByKeyword(keyword.trim());
        List<PaperListDto> paperListDtos = new ArrayList<>();

        for (Paper paper : papers) {
            Integer paperCount = saveRepository.countByPaper(paper);
            paperListDtos.add(PaperListDto.builder()
                    .title(paper.getTitle())
                    .id(paper.getId())
                    .author(paper.getAuthor())
                            .view(paper.getViews())
                            .save(paperCount)
                    .build());
        }
        return paperListDtos;
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


    /**
     * 논문 기본 정보 조회
     */
    public PaperDetailDto getPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId).orElse(null);
        if (paper == null) {
            throw new PaperNotFoundException("Paper not found");
        }
        int views = paper.getViews() + 1;
        return PaperDetailDto.builder()
                .title(paper.getTitle())
                .author(paper.getAuthor())
                .submitter(paper.getSubmitter())
                .comment(paper.getComment())
                .journal_ref(paper.getJournal_ref())
                .publication_date(paper.getPublicationDate())
                .views(views)
                .title_ko(paper.getTitle_ko())
                .build();
    }

    /**
     * 논문의 섹션 목록 조회
     */
    public List<SectionDto> getPaperSections(Long paperId) {

        // 논문 존재 여부 확인
        getPaper(paperId);

        List<Section> sections =
                sectionRepository.findByPaper_IdOrderBySectionOrderAsc(paperId);

        return sections.stream()
                .map(section -> {
                    Summary summary = section.getSummary();

                    SummaryDto summaryDto = null;

                    if (summary != null) {
                        summaryDto = SummaryDto.builder()
                                .summaryId(summary.getId())
                                .main(summary.isMain())
                                .content(summary.getContent())
                                .build();
                    }

                    return SectionDto.builder()
                            .sectionId(section.getId())
                            .sectionTitle(section.getSectionTitle())
                            .sectionOrder(section.getSectionOrder())
                            .sectionType((SEC_TYPE) section.getSectionType())
                            .content(section.getContent())
                            .contentKo(section.getContent_ko())
                            .summary(summaryDto)
                            .build();
                })
                .toList();
    }

    /**
     * 논문의 대표 AI 요약 조회
     */
    public SummaryDto getPaperSummary(Long paperId) {

        // 논문 존재 여부 확인
        getPaper(paperId);

        List<Section> sections =
                sectionRepository.findByPaper_IdOrderBySectionOrderAsc(paperId);

        return sections.stream()
                .map(Section::getSummary)
                .filter(summary -> summary != null && summary.isMain())
                .findFirst()
                .map(summary -> SummaryDto.builder()
                        .summaryId(summary.getId())
                        .main(summary.isMain())
                        .content(summary.getContent())
                        .build())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 논문의 대표 요약이 없습니다. ID: " + paperId
                        )
                );
    }
}
