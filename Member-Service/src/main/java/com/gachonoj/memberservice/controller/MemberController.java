package com.gachonoj.memberservice.controller;

import com.gachonoj.memberservice.domain.dto.request.EmailRequestDto;
import com.gachonoj.memberservice.domain.dto.request.EmailVerificationRequestDto;
import com.gachonoj.memberservice.domain.dto.request.SignUpRequestDto;
import com.gachonoj.memberservice.common.response.CommonResponseDto;
import com.gachonoj.memberservice.domain.dto.response.NicknameVerificationResponseDto;
import com.gachonoj.memberservice.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    // 이메일 인증번호 전송
    @PostMapping("/email")
    public ResponseEntity<CommonResponseDto<String>> sendEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        //결과 코드 로그
        log.info("authCode: {} ", memberService.joinEmail(emailRequestDto.getMemberEmail()));
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 이메일 인증번호 확인
    @PostMapping("/email/verification")
    public ResponseEntity<CommonResponseDto<String>> verifyEmail(@RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
        memberService.verifyEmail(emailVerificationRequestDto.getMemberEmail(), emailVerificationRequestDto.getAuthCode());
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    //회원가입
    @PostMapping("/members")
    public ResponseEntity<CommonResponseDto<String>> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        memberService.signUp(signUpRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 닉네임 중복 확인
    @GetMapping("/verification/{memberNickname}")
    public ResponseEntity<CommonResponseDto<NicknameVerificationResponseDto>> verifyMemberNickname(@PathVariable String memberNickname) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.verifyMemberNickname(memberNickname)));
    }
}
