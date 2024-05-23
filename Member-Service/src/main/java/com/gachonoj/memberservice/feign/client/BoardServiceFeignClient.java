package com.gachonoj.memberservice.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Board-Service")
public interface BoardServiceFeignClient {
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @DeleteMapping(value = "/board/member")
    void deleteBoardByMemberId(@RequestParam("memberId") Long memberId);
}
