package com.gachonoj.problemservice.feign.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Member-Service")
public interface MemberServiceFeignClient{
    // memberId로 푼 문제 수 조회
    @GetMapping(value = "/submission/member/solved")
    Integer getMemberSolved(@RequestParam("memberId") Long memberId);
}
