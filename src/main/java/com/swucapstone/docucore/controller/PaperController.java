package com.swucapstone.docucore.controller;

import com.swucapstone.docucore.config.CustomUserDetails;
import com.swucapstone.docucore.dto.PaperListDto;
import com.swucapstone.docucore.dto.SectionDto;
import com.swucapstone.docucore.dto.SummaryDto;
import com.swucapstone.docucore.entity.Save;
import com.swucapstone.docucore.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/papers")
@Controller
public class PaperController {

    private final PaperService paperService;

    // 논문 섹션별 본문 및 요약 조회
    @GetMapping("/{paperId}/sections")
    public ResponseEntity<List<SectionDto>> getPaperSections(
            @PathVariable Long paperId) {

        List<SectionDto> sections =
                paperService.getPaperSections(paperId);

        return ResponseEntity.ok(sections);
    }

    // 논문 대표 요약 조회
    @GetMapping("/{paperId}/summary")
    public ResponseEntity<SummaryDto> getPaperSummary(
            @PathVariable Long paperId) {

        SummaryDto summary =
                paperService.getPaperSummary(paperId);

        return ResponseEntity.ok(summary);
    }

    // 논문 검색
    @GetMapping("/search")
    public ResponseEntity<List<PaperListDto>> search(@RequestParam String query) {
        List<PaperListDto> paperListDtos = paperService.search(query);
        return ResponseEntity.ok(paperListDtos);
    }

    // 논문 저장
    @PatchMapping("/{paperId}/save")
    public ResponseEntity<Boolean> savePaper(@PathVariable Long paperId, @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        boolean saved = paperService.saveById(paperId, userId);
        return ResponseEntity.ok(saved);
    }
}