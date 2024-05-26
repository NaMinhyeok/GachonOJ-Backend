package com.gachonoj.problemservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "Ai-Service")
public interface AiServiceFeignClient {
    // 문제에 대한 ai 분석 삭제
    @DeleteMapping("/ai/problem")
    void deleteAiByProblemId(Long problemId);
}
