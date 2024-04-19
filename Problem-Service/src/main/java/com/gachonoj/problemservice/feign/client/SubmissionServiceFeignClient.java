package com.gachonoj.problemservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // problemId로 정답 제출 인원 조회
    @GetMapping(value = "/submission/problem/people")
    Integer getCorrectSubmission(@RequestParam("problemId") Long problemId);
    // problemId로 정답률 조회
    @GetMapping(value = "/submission/problem/rate")
    Integer getProblemCorrectRate(@RequestParam("problemId") Long problemId);
}

