package com.swucapstone.docucore.service;

import com.swucapstone.docucore.config.JwtTokenProvider;
import com.swucapstone.docucore.dto.LoginRequestDto;
import com.swucapstone.docucore.dto.SignUpRequestDto;
import com.swucapstone.docucore.dto.SignUpResponseDto;
import com.swucapstone.docucore.entity.User;
import com.swucapstone.docucore.exception.PasswardNotSameException;
import com.swucapstone.docucore.exception.UserAlreadyExistsException;
import com.swucapstone.docucore.exception.UserNameAlreadyExistsException;
import com.swucapstone.docucore.exception.UserNotFoundException;
import com.swucapstone.docucore.repository.SaveRepository;
import com.swucapstone.docucore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SaveRepository saveRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    // 회원가입
    @Transactional
    public SignUpResponseDto signup(SignUpRequestDto signUpDto) {
        if(userRepository.findUserByEmail(signUpDto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        if(userRepository.findUserByName(signUpDto.getName()).isPresent()){
            throw new UserNameAlreadyExistsException();
        }

        if(signUpDto.getPassword().equals(signUpDto.getPasswordConfirm())){
            throw new PasswardNotSameException();
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());

        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setPassword(encodedPassword);
        user.setName(signUpDto.getName());
        userRepository.save(user);

        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();
        signUpResponseDto.setUserId(user.getId());

        return signUpResponseDto;
    }

    // 이메일 중복 확인
    public Boolean checkEmail(String email) {
        Boolean check = userRepository.findUserByEmail(email).isEmpty();
        return check;
    }

    // 닉네임 중복 확인
    public Boolean checkName(String name) {
        Boolean check = userRepository.findUserByName(name).isEmpty();
        return check;
    }

    // 로그인
    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findUserByEmail(loginRequestDto.getEmail()).orElseThrow(UserNotFoundException::new); // 이메일 없을 경우 404

        // 403 오류
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateJwtToken(loginRequestDto.getEmail());
        return token;

    }

    @Transactional
    public String update(Long userId, String newName) {
        if(userRepository.findById(userId).isPresent() && !newName.isEmpty()){
            User user = userRepository.findById(userId).get();
            user.setName(newName);
            userRepository.save(user);

            return "이름 변경:" + user.getName() + " -> " + newName;
        }
        throw new UserNotFoundException();
    }
}
