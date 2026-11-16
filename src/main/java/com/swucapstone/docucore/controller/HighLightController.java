package com.swucapstone.docucore.controller;
import com.swucapstone.docucore.dto.HighLightRequest;
import com.swucapstone.docucore.dto.HighLightResponse;
import com.swucapstone.docucore.service.HighLightService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/papers/{paperId}/sections/{sectionId}/highlights")
public class HighLightController {

    private final HighLightService highLightService;

    // 하이라이트 등록
    @PostMapping
    public ResponseEntity<HighLightResponse> createHighLight(
            @PathVariable Long paperId,
            @PathVariable Long sectionId,
            @RequestBody HighLightRequest request
    ) {
        HighLightResponse response =
                highLightService.createHighLight(
                        paperId, sectionId, request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 특정 섹션의 사용자별 하이라이트 조회
    @GetMapping
    public ResponseEntity<List<HighLightResponse>> getHighLights(
            @PathVariable Long paperId,
            @PathVariable Long sectionId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                highLightService.getHighLights(
                        paperId, sectionId, userId
                )
        );
    }

    // 하이라이트 삭제
    @DeleteMapping("/{highlightId}")
    public ResponseEntity<Void> deleteHighLight(
            @PathVariable Long highlightId,
            @RequestParam Long userId
    ) {
        highLightService.deleteHighLight(highlightId, userId);

        return ResponseEntity.noContent().build();
    }
}
