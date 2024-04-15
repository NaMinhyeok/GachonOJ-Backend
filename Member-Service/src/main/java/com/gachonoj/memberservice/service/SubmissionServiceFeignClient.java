package com.gachonoj.memberservice.service;

import com.gachonoj.memberservice.domain.dto.response.SubmissionMemberInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    @GetMapping(value = "submission/member/info")
    SubmissionMemberInfoResponseDto getMemberInfoBySubmission(@RequestParam("memberId") Long memberId);
}
