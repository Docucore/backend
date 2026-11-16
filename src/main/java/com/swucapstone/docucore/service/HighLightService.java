package com.swucapstone.docucore.service;


import com.swucapstone.docucore.dto.HighLightRequest;
import com.swucapstone.docucore.dto.HighLightResponse;
import com.swucapstone.docucore.entity.HighLight;
import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Section;
import com.swucapstone.docucore.entity.User;
import com.swucapstone.docucore.repository.HighLightRepository;
import com.swucapstone.docucore.repository.PaperRepository;
import com.swucapstone.docucore.repository.SectionRepository;
import com.swucapstone.docucore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HighLightService {

    private final HighLightRepository highLightRepository;
    private final PaperRepository paperRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;

    // 하이라이트 등록
    public HighLightResponse createHighLight(
            Long paperId,
            Long sectionId,
            HighLightRequest request
    ) {

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "논문을 찾을 수 없습니다."
                ));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "섹션을 찾을 수 없습니다."
                ));

        // 섹션이 요청한 논문에 속하는지 확인
        if (!section.getPaper().getId().equals(paperId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "해당 논문에 속하지 않는 섹션입니다."
            );
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));

        String content = section.getContent_ko();

        if (content == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "번역된 섹션 내용이 없습니다."
            );
        }

        int start = request.getStart();
        int end = request.getEnd();

        // 글자 위치 검증
        if (start < 0 || end <= start || end > content.length()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바르지 않은 하이라이트 범위입니다."
            );
        }

        HighLight highLight = HighLight.builder()
                .paper(paper)
                .section(section)
                .user(user)
                .start(start)
                .end(end)
                .build();

        return HighLightResponse.from(
                highLightRepository.save(highLight)
        );
    }

    // 특정 섹션의 사용자별 하이라이트 조회
    @Transactional(readOnly = true)
    public List<HighLightResponse> getHighLights(
            Long paperId,
            Long sectionId,
            Long userId
    ) {

        return highLightRepository
                .findByUser_IdAndPaper_IdAndSection_IdOrderByStartAsc(
                        userId, paperId, sectionId
                )
                .stream()
                .map(HighLightResponse::from)
                .toList();
    }

    // 하이라이트 삭제
    public void deleteHighLight(Long highlightId, Long userId) {

        HighLight highLight = highLightRepository.findById(highlightId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "하이라이트를 찾을 수 없습니다."
                ));

        if (!highLight.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인의 하이라이트만 삭제할 수 있습니다."
            );
        }

        highLightRepository.delete(highLight);
    }
}