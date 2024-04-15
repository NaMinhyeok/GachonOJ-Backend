package com.gachonoj.memberservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    @GetMapping(value = "/problem/member/bookmark")
    Integer getBookmarkCountByMemberId(@RequestParam("memberId") Long memberId);
}
