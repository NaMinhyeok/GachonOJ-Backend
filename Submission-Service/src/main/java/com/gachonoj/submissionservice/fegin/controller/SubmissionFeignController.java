package com.gachonoj.submissionservice.fegin.controller;

import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.fegin.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.fegin.service.SubmissionFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    // 정답자 제출 인원 조회
    @GetMapping("/problem/people")
    public Integer getCorrectSubmission(Long problemId) {
        return submissionFeignService.getCorrectSubmission(problemId);
    }

    // 정답률 조회
    @GetMapping("/problem/rate")
    public double getProblemCorrectRate(Long problemId) {
        return submissionFeignService.getProblemCorrectRate(problemId);
    }

    // 틀린 문제 조회
    @GetMapping("problem/incorrect")
    public List<Long> getIncorrectProblemIds(@RequestParam Long memberId) {
        return submissionFeignService.getIncorrectProblemIdsByMemberId(memberId);
    }

    // 맞춘 문제 조회
    @GetMapping("problem/correct")
    public List<Long> getCorrectProblemIds(@RequestParam Long memberId) {
        return submissionFeignService.getCorrectProblemIdsByMemberId(memberId);
    }
    // 총 제출 수 조회
    @GetMapping("/problem/submitcount")
    public Integer getProblemSubmitCount(Long problemId) {
        return submissionFeignService.getProblemSubmitCount(problemId);
    }
}
