package com.gachonoj.boardservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "member-service")
public interface MemberServiceFeignClient {
    @GetMapping("/member/nickname/{memberId}")
    String getNicknames(@PathVariable Long memberId);
}
