package com.gachonoj.aiservice.feign.controller;


import com.gachonoj.aiservice.feign.service.AiFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiFeignController {

    private final AiFeignService aiFeignService;

    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @DeleteMapping(value = "/member")
    public void deleteAiByMemberId(@RequestParam("memberId") Long memberId) {
        aiFeignService.deleteAiByMemberId(memberId);
    }
    // 문제에 대한 ai 분석 삭제
    @DeleteMapping(value = "/problem")
    public void deleteAiByProblemId(@RequestParam("problemId") Long problemId) {
        aiFeignService.deleteAiByProblemId(problemId);
    }
}
