package com.gachonoj.aiservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Member-Service")
public interface MemberServiceFeignClient {
    // 사용자의 닉네임 조회
    @GetMapping("/member/nickname/{memberId}")
    String getNickname(@PathVariable Long memberId);
}
