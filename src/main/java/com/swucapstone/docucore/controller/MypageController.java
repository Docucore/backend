package com.swucapstone.docucore.controller;

import com.swucapstone.docucore.config.CustomUserDetails;
import com.swucapstone.docucore.dto.MypageDto;
import com.swucapstone.docucore.dto.SavedPaperDto;
import com.swucapstone.docucore.entity.User;
import com.swucapstone.docucore.exception.UserNotFoundException;
import com.swucapstone.docucore.repository.UserRepository;
import com.swucapstone.docucore.service.PaperService;
import com.swucapstone.docucore.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {
    private final PaperService paperService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<MypageDto> getMypage(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        User userFounded = userRepository.findById(userId).orElse(null);
        if (userFounded == null) {
            throw new UserNotFoundException();
        }
        MypageDto dto = MypageDto.builder()
                .userId(userFounded.getId())
                .name(userFounded.getName())
                .email(userFounded.getEmail()).build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/save")
    public ResponseEntity<List<SavedPaperDto>> getSavedPapers(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        List<SavedPaperDto> savedPaperDtos = paperService.savedPapers(userId);
        return ResponseEntity.ok(savedPaperDtos);
    }

    @GetMapping("/read")
    public ResponseEntity<List<SavedPaperDto>> getReadingPapers(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        List<SavedPaperDto> reading = paperService.readingPapers(userId);
        return ResponseEntity.ok(reading);
    }

    @PutMapping("/rename")
    public ResponseEntity<String> updateUserName(@AuthenticationPrincipal CustomUserDetails user, @RequestParam String newName) {
        Long userId = user.getUserId();
        String result = userService.update(userId, newName);
        return ResponseEntity.ok(result);
    }


}
