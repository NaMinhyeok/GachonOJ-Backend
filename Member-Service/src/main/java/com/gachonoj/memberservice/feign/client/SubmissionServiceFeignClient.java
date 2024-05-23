package com.gachonoj.memberservice.feign.client;

import com.gachonoj.memberservice.feign.dto.response.SubmissionMemberInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // memberId로 푼 문제수, 시도한 문제 수 조회
    @GetMapping(value = "/submission/member/info")
    SubmissionMemberInfoResponseDto getMemberInfoBySubmission(@RequestParam("memberId") Long memberId);
    // memberId로 푼 문제 수 조회
    @GetMapping(value = "/submission/member/solved")
    Integer getMemberSolved(@RequestParam("memberId") Long memberId);
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @DeleteMapping(value = "/submission/member")
    void deleteSubmissionByMemberId(@RequestParam("memberId") Long memberId);
}
