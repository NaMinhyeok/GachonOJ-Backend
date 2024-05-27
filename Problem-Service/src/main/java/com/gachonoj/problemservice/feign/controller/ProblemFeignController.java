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
    // 시험 문제의 점수 조회
    @GetMapping("/question/{problemId}/score")
    public Integer getQuestionScore(@PathVariable Long problemId) {
        return problemFeignService.getQuestionScore(problemId);
    }
    // Test 엔티티에 TestScore 저장
    @PostMapping("/test/{examId}/score")
    public void getTestScore(@PathVariable Long examId,@RequestParam Long memberId,@RequestBody Integer testScore) {
        problemFeignService.getTestScore(examId, memberId, testScore);
    }
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @DeleteMapping("/member")
    public void deleteProblemByMemberId(@RequestParam Long memberId) {
        problemFeignService.deleteProblemByMemberId(memberId);
    }
    // 문제의 상태가 REGISTERED인 문제 갯수 조회
    @GetMapping("/registered/count")
    public Integer getRegisteredProblemCount(@RequestParam List<Long> problemIds) {
        return problemFeignService.getRegisteredProblemCount(problemIds);
    }
}
