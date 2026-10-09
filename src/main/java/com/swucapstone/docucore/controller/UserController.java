package com.swucapstone.docucore.controller;

import com.swucapstone.docucore.dto.LoginRequestDto;
import com.swucapstone.docucore.dto.SignUpRequestDto;
import com.swucapstone.docucore.dto.SignUpResponseDto;
import com.swucapstone.docucore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Controller
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpDto){
        SignUpResponseDto response = userService.signup(signUpDto);
        return ResponseEntity.ok(response);
    }

    // 이메일 중복 확인
    @GetMapping("/checkemail")
    ResponseEntity<Boolean> check(@RequestParam String email){
        Boolean response = userService.checkEmail(email);
        return ResponseEntity.ok(response);
    }

    // 닉네임 중복 확인
    @GetMapping("/checkname")
    ResponseEntity<Boolean> checkName(@RequestParam String name){
        Boolean response = userService.checkName(name);
        return ResponseEntity.ok(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = userService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }

}
