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
    // 공개된 테스트케이스 조회
    @GetMapping("/{problemId}/testcase/visible")
    public List<SubmissionProblemTestCaseResponseDto> getVisibleTestCases(@PathVariable Long problemId) {
        return problemFeignService.getVisibleTestCases(problemId);
    }
    // 문제 점수 조회
    @GetMapping("/{problemId}/score")
    public Integer getProblemScore(@PathVariable Long problemId) {
        return problemFeignService.getProblemScore(problemId);
    }
    // 문제 ID로 문제 프롬프트 가져오기
    @GetMapping("/prompt/{problemId}")
    public String getProblemPromptByProblemId(@PathVariable Long problemId) {
        return problemFeignService.getProblemPromptByProblemId(problemId);
    }
    // 문제 time limit 가져오기
    @GetMapping("/{problemId}/time-limit")
    public Integer getProblemTimeLimit(@PathVariable Long problemId) {
        return problemFeignService.getProblemTimeLimit(problemId);
    }
    // 문제 제목 가져오기
    @GetMapping("/{problemId}/title")
    public String getProblemTitle(@PathVariable Long problemId) {
        return problemFeignService.getProblemTitle(problemId);
    }
}
