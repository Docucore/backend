package com.swucapstone.docucore.service;


import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Progress;
import com.swucapstone.docucore.entity.Section;
import com.swucapstone.docucore.entity.User;
import com.swucapstone.docucore.exception.PaperNotFoundException;
import com.swucapstone.docucore.exception.UserNotFoundException;
import com.swucapstone.docucore.repository.PaperRepository;
import com.swucapstone.docucore.repository.ProgressRepository;
import com.swucapstone.docucore.repository.SectionRepository;
import com.swucapstone.docucore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final SectionRepository sectionRepository;

    /**
     * 논문 읽기 종료 시 읽기 기록 및 진행률 저장
     */
    @Transactional
    public void saveReadingProgress(
            Long userId,
            Long paperId,
            int sectionNumber
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new PaperNotFoundException("Paper not  found"));

        // 현재 논문의 전체 섹션 수
        long totalSections = sectionRepository.countByPaper(paper);

        if (totalSections == 0) {
            throw new IllegalStateException("논문에 섹션이 없습니다.");
        }

        // 현재 읽은 섹션 조회
        Section section = sectionRepository
                .findByPaperAndSectionOrder(paper, sectionNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 섹션입니다.")
                );

        // 진행률 계산: 현재 섹션 번호 / 전체 섹션 수 * 100
        int runningRate = (int) Math.round(
                (double) sectionNumber / totalSections * 100
        );

        // 진행률 범위 보정
        runningRate = Math.min(100, Math.max(0, runningRate));

        // 기존 기록이 있으면 갱신, 없으면 새로 생성
        int finalRunningRate = runningRate;
        Progress progress = progressRepository
                .findByPaperAndUser(paper, user)
                .orElseGet(() -> Progress.builder()
                        .user(user)
                        .paper(paper)
                        .sections(section)
                        .running_rate(finalRunningRate)
                        .build()
                );

        progressRepository.save(progress);
    }
}
