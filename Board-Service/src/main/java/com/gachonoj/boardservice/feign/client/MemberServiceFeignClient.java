package com.gachonoj.boardservice.feign.client;

import com.gachonoj.boardservice.feign.dto.MemberNicknamesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "member-service")
public interface MemberServiceFeignClient {
    @GetMapping("/member/nickname/{memberId}")
    String getNicknames(@PathVariable Long memberId);

    @GetMapping("/member/nickname")
    List<MemberNicknamesDto> getNicknames(@RequestParam List<Long> memberIds);
}
