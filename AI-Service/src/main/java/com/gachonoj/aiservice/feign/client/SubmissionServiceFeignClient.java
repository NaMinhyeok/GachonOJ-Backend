package com.gachonoj.aiservice.feign.client;

import com.gachonoj.aiservice.feign.dto.response.SubmissionCodeInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // 제출 번호 통해서 제출 코드, 문제 ID 가져오기
    @GetMapping("/submission/code/{submissionId}")
    SubmissionCodeInfoResponseDto getSubmissionCodeBySubmissionId(@PathVariable Long submissionId);
}
