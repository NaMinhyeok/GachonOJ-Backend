package com.gachonoj.problemservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // problemId로 정답 제출 인원 조회
    @GetMapping(value = "/submission/problem/people")
    Integer getCorrectSubmission(@RequestParam("problemId") Long problemId);
    // problemId로 정답률 조회
    @GetMapping(value = "/submission/problem/rate")
    Double getProblemCorrectRate(@RequestParam("problemId") Long problemId);
    // 총 제출 수 조회
    @GetMapping(value = "/submission/problem/submitcount")
    Integer getProblemSubmitCount(@RequestParam("problemId") Long problemId);
    // memberId로 틀린 문제 조회
    @GetMapping("/submission/problem/incorrect")
    List<Long> getIncorrectProblemIds(@RequestParam Long memberId);

    @GetMapping("/submission/problem/correct")
    List<Long> getCorrectProblemIds(@RequestParam Long memberId);
}



