package com.gachonoj.problemservice.feign.client;


import com.gachonoj.memberservice.feign.dto.response.ProblemMemberInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Member-Service")
public interface MemberServiceFeignClient{
    // memberId로 푼 문제 수 조회
    @GetMapping(value = "/submission/member/solved")
    Integer getMemberSolved(@RequestParam("memberId") Long memberId);
    // 닉네임 조회
    @GetMapping("/member/nickname/{memberId}")
    String getNicknames(@PathVariable Long memberId);

    // memberId로 member 정보 조회
    @GetMapping("/member/{memberId}/info")
    ProblemMemberInfoResponseDto getMemberInfo(@PathVariable("memberId") Long memberId);
}
