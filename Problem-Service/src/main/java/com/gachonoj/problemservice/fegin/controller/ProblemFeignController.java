package com.gachonoj.problemservice.fegin.controller;

import com.gachonoj.problemservice.fegin.service.ProblemFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/problem")
public class ProblemFeignController {

    private final ProblemFeignService problemFeignService;

    // 사용자의 북마크 수 조회
    @GetMapping("/member/bookmark")
    public Integer getBookmarkCountByMemberId(@RequestParam Long memberId) {
        return problemFeignService.getBookmarkCountByMemberId(memberId);
    }

}
