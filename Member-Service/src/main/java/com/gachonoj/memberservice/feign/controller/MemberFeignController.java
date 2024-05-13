package com.gachonoj.memberservice.feign.controller;

import com.gachonoj.memberservice.feign.dto.response.ProblemMemberInfoResponseDto;
import com.gachonoj.memberservice.feign.dto.response.SubmissionMemberRankInfoResponseDto;
import com.gachonoj.memberservice.feign.service.MemberFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberFeignController {
    private final MemberFeignService memberFeignService;

    // 사용자의 닉네임 조회
    @GetMapping("/nickname/{memberId}")
    public String getNicknames(@PathVariable Long memberId) {
        return memberFeignService.getNicknameToBoard(memberId);
    }
    // 사용자의 memberRank 조회
    @GetMapping("/{memberId}/rank")
    public SubmissionMemberRankInfoResponseDto getMemberRank(@PathVariable Long memberId) {
        return memberFeignService.getMemberRank(memberId);
    }
    // 사용자의 memberRank 갱신
    @PostMapping("/{memberId}/rank")
    public void updateMemberRank(@PathVariable Long memberId, @RequestBody Integer newRank) {
        memberFeignService.updateMemberRank(memberId, newRank);
    }
    @GetMapping("/{memberId}/info")
    public ProblemMemberInfoResponseDto getMemberInfo(@PathVariable Long memberId) {
        return memberFeignService.getMemberInfo(memberId);
    }
}
