package com.gachonoj.submissionservice.fegin.controller;

import com.gachonoj.submissionservice.fegin.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.fegin.service.SubmissionFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionFeignController {
    private final SubmissionFeignService submissionFeignService;

    // 사용자 푼 문제, 시도한 문제 수 조회
    @GetMapping("/member/info")
    public SubmissionMemberInfoResponseDto getMemberInfo(Long memberId) {
        return submissionFeignService.getMemberInfo(memberId);
    }
    // 사용자 푼 문제 수 조회
    @GetMapping("/member/solved")
    public Integer getMemberSolved(Long memberId) {
        return submissionFeignService.getMemberSolved(memberId);
    }

}
