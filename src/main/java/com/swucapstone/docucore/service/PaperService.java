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
                            .category(paper.getCategory())
                    .build());
        }
        return paperListDtos;
    }

    public PaperReadDto read(Long id) {
        Paper paper = paperRepository.findById(id).orElse(null);
        return PaperReadDto.builder().build();
    }





    /**
     * 마이페이지 - 북마크한 논문 목록 조회
     */
    public List<SavedPaperDto> savedPapers(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        List<Save> saves = saveRepository.findByUser(user);


        return saves.stream()
                .map(save -> {
                    Paper paper = save.getPaper();

                    Progress progress = progressRepository.findByPaperAndUser(paper, user).orElse(null);

                    Integer saveCount = saveRepository.countByPaper(paper);

                    return SavedPaperDto.builder()
                            .paperId(paper.getId())
                            .title(paper.getTitle())
                            .author(paper.getAuthor())
                            .title_ko(paper.getTitle_ko())
                            .running_rate(progress != null ? progress.getRunning_rate() : 0)
                            .publication_date(paper.getPublicationDate().toString())
                            .view(paper.getViews())
                            .save(saveCount)
                            .build();
                })
                .toList();
    }


    public List<SavedPaperDto> readingPapers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        List<Progress> progresses = progressRepository.findByUser(user);

        return progresses.stream()
                .map(progress -> {
                    Paper paper = progress.getPaper();

                    return SavedPaperDto.builder()
                            .paperId(paper.getId())
                            .title(paper.getTitle())
                            .author(paper.getAuthor())
                            .title_ko(paper.getTitle_ko())
                            .running_rate(progress.getRunning_rate())
                            .publication_date(paper.getPublicationDate().toString())
                            .build();
                })
                .toList();
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


    /**
     * 논문 북마크 토글
     * @return true: 북마크 추가, false: 북마크 삭제
     */
    public boolean saveById(Long paperId, Long userId) {

        // 논문 존재 여부 확인
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found"));

        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 이미 북마크한 논문인지 확인
        Save save = saveRepository.findByPaperAndUser(paper, user)
                .orElse(null);

        if (save != null) {
            // 이미 저장되어 있다면 북마크 삭제
            saveRepository.delete(save);
            return false;
        }

        // 저장되어 있지 않다면 북마크 추가
        Save newSave = Save.builder()
                .user(user)
                .paper(paper)
                .build();

        saveRepository.save(newSave);
        return true;
    }

}
