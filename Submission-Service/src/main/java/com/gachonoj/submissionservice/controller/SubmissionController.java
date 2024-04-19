package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.domain.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    /*
    * feign client를 이용하는 API 작성
    * */
    // 사용자 푼 문제, 시도한 문제 수 조회
    @GetMapping("/member/info")
    public SubmissionMemberInfoResponseDto getMemberInfo(Long memberId) {
        return submissionService.getMemberInfo(memberId);
    }
    // 사용자 푼 문제 수 조회
    @GetMapping("/solved")
    public Integer getMemberSolved(Long memberId) {
        return submissionService.getMemberSolved(memberId);
    }
    /*
    * 자체적으로 사용 하는 API 작성
    * */
}
