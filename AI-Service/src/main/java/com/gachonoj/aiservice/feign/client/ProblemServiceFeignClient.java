package com.gachonoj.aiservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    // 문제 ID로 문제 프롬프트 가져오기
    @GetMapping("/problem/prompt/{problemId}")
    String getProblemPromptByProblemId(@PathVariable Long problemId);
}
