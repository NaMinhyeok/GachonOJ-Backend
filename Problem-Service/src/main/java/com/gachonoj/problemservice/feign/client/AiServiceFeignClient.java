package com.gachonoj.problemservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "Ai-Service")
public interface AiServiceFeignClient {
    // 문제에 대한 ai 분석 삭제
    void deleteAiByProblemId(Long problemId);
}
