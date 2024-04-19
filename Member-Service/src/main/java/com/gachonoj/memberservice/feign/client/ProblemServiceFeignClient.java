package com.gachonoj.memberservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    // memberId로 북마크한 문제 수 조회
    @GetMapping(value = "/problem/member/bookmark")
    Integer getBookmarkCountByMemberId(@RequestParam("memberId") Long memberId);
}
