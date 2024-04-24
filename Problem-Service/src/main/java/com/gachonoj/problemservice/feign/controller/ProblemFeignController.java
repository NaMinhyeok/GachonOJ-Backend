package com.gachonoj.problemservice.feign.controller;

import com.gachonoj.problemservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import com.gachonoj.problemservice.feign.service.ProblemFeignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // 테스트케이스 조회
    @GetMapping("/{problemId}/testcase")
    public List<SubmissionProblemTestCaseResponseDto> getTestCases(@PathVariable Long problemId) {
        return problemFeignService.getTestCases(problemId);
    }
}
