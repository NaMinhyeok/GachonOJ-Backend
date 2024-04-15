package com.gachonoj.memberservice.controller;

import com.gachonoj.memberservice.domain.dto.request.EmailRequestDto;
import com.gachonoj.memberservice.domain.dto.request.EmailVerificationRequestDto;
import com.gachonoj.memberservice.domain.dto.request.MemberLangRequestDto;
import com.gachonoj.memberservice.domain.dto.request.SignUpRequestDto;
import com.gachonoj.memberservice.common.response.CommonResponseDto;
import com.gachonoj.memberservice.domain.dto.response.*;
import com.gachonoj.memberservice.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    // 호버 창 정보 조회
    @GetMapping("/hover")
    public ResponseEntity<CommonResponseDto<HoverResponseDto>> getHoverInfo(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getHoverInfo(memberId)));
    }
    // 사용자 정보 조회
    @GetMapping("/info")
    public ResponseEntity<CommonResponseDto<MemberInfoResponseDto>> getMemberInfo(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberInfo(memberId)));
    }
    // 사용자 본인 정보 수정
    //사용자 선호 언어 조회
    @GetMapping("/lang")
    public ResponseEntity<CommonResponseDto<MemberLangResponseDto>> getMemberLang(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberLang(memberId)));
    }
    //사용자 선호 언어 변경
    @PutMapping("/lang")
    public ResponseEntity<CommonResponseDto<Void>> updateMemberLang(HttpServletRequest request, @RequestBody MemberLangRequestDto memberLangRequestDto) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        memberService.updateMemberLang(memberId,memberLangRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    //대회 페이지 사용자 정보
    @GetMapping("/info/exam")
    public ResponseEntity<CommonResponseDto<MemberInfoExamResponseDto>> getMemberInfoExam(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberInfoExam(memberId)));
    }
}
