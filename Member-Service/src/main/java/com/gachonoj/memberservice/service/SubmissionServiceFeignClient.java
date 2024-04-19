package com.gachonoj.memberservice.service;

import com.gachonoj.memberservice.domain.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.memberservice.domain.entity.Member;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // memberId로 푼 문제수, 시도한 문제 수 조회
    @GetMapping(value = "submission/member/info")
    SubmissionMemberInfoResponseDto getMemberInfoBySubmission(@RequestParam("memberId") Long memberId);
    // memberId로 푼 문제 수 조회
    @GetMapping(value = "submission/solved")
    Integer getMemberSolved(@RequestParam("memberId") Long memberId);
}
