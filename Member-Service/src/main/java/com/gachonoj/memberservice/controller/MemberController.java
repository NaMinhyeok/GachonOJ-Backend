package com.gachonoj.memberservice.controller;

import com.gachonoj.memberservice.domain.dto.request.EmailRequestDto;
import com.gachonoj.memberservice.domain.dto.request.EmailVerificationRequestDto;
import com.gachonoj.memberservice.domain.dto.request.LoginRequestDto;
import com.gachonoj.memberservice.domain.dto.request.SignUpRequestDto;
import com.gachonoj.memberservice.domain.dto.response.CommonResponseDto;
import com.gachonoj.memberservice.domain.dto.response.LoginResponseDto;
import com.gachonoj.memberservice.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    // 이메일 인증번호 전송
    @PostMapping("/email")
    public ResponseEntity<CommonResponseDto<String>> sendEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        //결과 코드 로그
        log.info("authCode: {} ", memberService.joinEmail(emailRequestDto.getMemberEmail()));
        return ResponseEntity.ok(CommonResponseDto.success(emailRequestDto.getMemberEmail()));
    }
    // 이메일 인증번호 확인
    @PostMapping("/email/verification")
    public ResponseEntity<CommonResponseDto<String>> verifyEmail(@RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
        if(memberService.verifyEmail(emailVerificationRequestDto.getMemberEmail(), emailVerificationRequestDto.getAuthCode())) {
            log.info("인증 성공");
            return ResponseEntity.ok(CommonResponseDto.success());
        } else {
            log.info("인증 실패");
            return ResponseEntity.ok(CommonResponseDto.fail(400, "인증번호가 일치하지 않습니다."));
        }
    }
    //회원가입
    @PostMapping("/members")
    public ResponseEntity<CommonResponseDto<String>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        memberService.signUp(signUpRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
        log.info(loginResponseDto.toString());
        return ResponseEntity.ok(CommonResponseDto.success(loginResponseDto));
    }
}
