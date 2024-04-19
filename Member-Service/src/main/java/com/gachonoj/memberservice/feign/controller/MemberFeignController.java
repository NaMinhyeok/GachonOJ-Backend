package com.gachonoj.memberservice.feign.controller;

import com.gachonoj.memberservice.feign.service.MemberFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberFeignController {
    private final MemberFeignService memberFeignService;

    // 사용자의 닉네임 조회
    @GetMapping("/nickname/{memberId}")
    public String getNicknames(@PathVariable Long memberId) {
        return memberFeignService.getNicknameToBoard(memberId);
    }

}
