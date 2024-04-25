package com.gachonoj.submissionservice.fegin.client;

import com.gachonoj.submissionservice.fegin.dto.response.SubmissionProblemTestCaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    // 테스트케이스 조회
     @GetMapping("/problem/{problemId}/testcase")
     List<SubmissionProblemTestCaseResponseDto> getTestCases(@PathVariable Long problemId);
}
