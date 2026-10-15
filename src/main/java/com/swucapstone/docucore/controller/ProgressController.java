package com.swucapstone.docucore.controller;

import com.swucapstone.docucore.config.CustomUserDetails;
import com.swucapstone.docucore.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/exit")
    public ResponseEntity<Void> saveProgressOnExit(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam Long paperId,
            @RequestParam int sectionNumber
    ) {
        Long userId = user.getUserId();
        progressService.saveReadingProgress(
                userId,
                paperId,
                sectionNumber
        );

        return ResponseEntity.noContent().build();
    }
}