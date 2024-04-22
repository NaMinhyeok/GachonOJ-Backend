package com.gachonoj.memberservice.controller;

import com.gachonoj.memberservice.domain.constant.Role;
import com.gachonoj.memberservice.domain.dto.request.*;
import com.gachonoj.memberservice.common.response.CommonResponseDto;
import com.gachonoj.memberservice.domain.dto.response.*;
import com.gachonoj.memberservice.service.MemberService;

import com.gachonoj.memberservice.service.S3UploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    @PostMapping("/verification/nickname")
    public ResponseEntity<CommonResponseDto<NicknameVerificationResponseDto>> verifyMemberNickname(@RequestBody MemberNicknameRequestDto memberNicknameRequestDto) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.verifiedMemberNickname(memberNicknameRequestDto)));
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
    //랭킹 사용자 정보 확인
    @GetMapping("/info/ranking")
    public ResponseEntity<CommonResponseDto<MemberInfoRankingResponseDto>> getMemberInfoRanking(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberInfoRanking(memberId)));
    }
    // 사용자 목록 조회
    @GetMapping("/admin/members/list")
    public ResponseEntity<CommonResponseDto<Page<MemberListResponseDto>>> getMemberList(@RequestParam String memberRole,
                                                                                        @RequestParam(required = false,defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberList(memberRole, pageNo)));
    }
    // 사용자 랭킹 목록 조회
    @GetMapping("/ranking")
    public ResponseEntity<CommonResponseDto<Page<MemberRankingResponseDto>>> getMemberRankingList(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                                  @RequestParam(required = false) String search) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberRankingList(pageNo,search)));
    }
    // 사용자 정보 수정
    @PutMapping("/info")
    public ResponseEntity<CommonResponseDto<Void>> updateMemberInfo(HttpServletRequest request, @RequestPart(required = false, name = "img") MultipartFile memberImg, @RequestPart(name = "info") MemberInfoRequestDto memberInfoRequestDto) throws IOException {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        memberService.updateMemberInfo(memberId, memberImg, memberInfoRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 회원 탈퇴
    @DeleteMapping("/member")
    public ResponseEntity<CommonResponseDto<Void>> deleteMember(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<CommonResponseDto<Void>> updatePassword(HttpServletRequest request, @RequestBody @Valid UpdatePasswordRequestDto updatePasswordRequestDto ) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        memberService.updateMemberPassword(memberId, updatePasswordRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 사용자 추가 생성
    @PostMapping("/admin/members")
    public ResponseEntity<CommonResponseDto<Void>> createMember(@RequestBody @Valid CreateMemberRequestDto createMemberRequestDto) {
        memberService.createMember(createMemberRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 관리자가 사용자 정보 변경
    @PutMapping("/admin/members/{memberId}")
    public ResponseEntity<CommonResponseDto<Void>> updateMember(@RequestBody @Valid UpdateMemberRequestDto updateMemberRequestDto,@PathVariable Long memberId) {
        memberService.updateMemberByAdmin(updateMemberRequestDto,memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 관리자 화면 사용자 정보변경을 위한 사용자 정보 조회
    @GetMapping("/admin/members/{memberId}")
    public ResponseEntity<CommonResponseDto<MemberInfoByAdminResponseDto>> getMemberInfoByAdmin(@PathVariable Long memberId) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberInfoByAdmin(memberId)));
    }
    // 관리자가 회원 탈퇴 시키기
    @DeleteMapping("/admin/members/{memberId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteMemberByAdmin(@PathVariable Long memberId) {
        memberService.deleteMemberByAdmin(memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 응시자 정보 조회 이메일 검색, 학번 검색
    @GetMapping("/info/test")
    public ResponseEntity<CommonResponseDto<List<MemberInfoTestResponseDto>>> getMemberInfoTest(@RequestParam(required = false) String memberEmail,
                                                                                           @RequestParam(required = false) String memberNumber) {
        return ResponseEntity.ok(CommonResponseDto.success(memberService.getMemberInfoTest(memberEmail, memberNumber)));
    }
    //로그아웃 구현
    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto<Void>> logout(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        memberService.logout(memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
}
