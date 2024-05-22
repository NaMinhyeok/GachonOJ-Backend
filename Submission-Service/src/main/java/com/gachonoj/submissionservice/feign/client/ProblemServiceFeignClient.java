package com.gachonoj.submissionservice.feign.client;

import com.gachonoj.submissionservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "Problem-Service")
public interface ProblemServiceFeignClient {
    // 테스트케이스 조회
    @GetMapping("/problem/{problemId}/testcase")
    List<SubmissionProblemTestCaseResponseDto> getTestCases(@PathVariable Long problemId);
    // 공개된 테스트케이스 조회
    @GetMapping("/problem/{problemId}/testcase/visible")
    List<SubmissionProblemTestCaseResponseDto> getVisibleTestCases(@PathVariable Long problemId);
    // 문제 점수 조회
    @GetMapping("/problem/{problemId}/score")
    Integer getProblemScore(@PathVariable Long problemId);
    // 문제 time limit 가져오기
    @GetMapping("/problem/{problemId}/time-limit")
    Integer getProblemTimeLimit(@PathVariable Long problemId);
    // 문제 제목 가져오기
    @GetMapping("/problem/{problemId}/title")
    String getProblemTitle(@PathVariable Long problemId);
    // 시험 문제의 점수 조회
    @GetMapping("/problem/question/{problemId}/score")
    Integer getQuestionScore(@PathVariable Long problemId);
    // Test 엔티티에 TestScore 저장
    @PostMapping("/problem/test/{examId}/score")
    Void saveTestScore(@PathVariable Long examId, @RequestParam Long memberId, @RequestBody Integer testScore);
}
