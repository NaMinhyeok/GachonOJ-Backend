package com.gachonoj.submissionservice.feign.client;

import com.gachonoj.submissionservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    // 테스트케이스 조회
    @GetMapping("/problem/{problemId}/testcase")
    List<SubmissionProblemTestCaseResponseDto> getTestCases(@PathVariable Long problemId);
    // 공개된 테스트케이스 조회
    @GetMapping("/problem/{problemId}/testcase/visible")
    List<SubmissionProblemTestCaseResponseDto> getVisibleTestCases(@PathVariable Long problemId);
    // 문제 점수 조회
    @GetMapping("/problem/{problemId}/score")
    Integer getProblemScore(@PathVariable Long problemId);
}
