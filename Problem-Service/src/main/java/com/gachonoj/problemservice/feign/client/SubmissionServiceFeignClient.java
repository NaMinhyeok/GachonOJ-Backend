package com.gachonoj.problemservice.feign.client;

import com.gachonoj.problemservice.feign.dto.response.SubmissionExamResultInfoResponseDto;
import com.gachonoj.problemservice.feign.dto.response.CorrectRateResponseDto;
import com.gachonoj.problemservice.feign.dto.response.SubmissionResultCountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Submission-Service")
public interface SubmissionServiceFeignClient {
    // problemId로 정답 제출 인원 조회
    @GetMapping(value = "/submission/problem/people")
    Integer getCorrectSubmission(@RequestParam("problemId") Long problemId);
    // problemId로 정답률 조회
    @GetMapping(value = "/submission/problem/rate")
    Double getProblemCorrectRate(@RequestParam("problemId") Long problemId);
    // 총 제출 수 조회
    @GetMapping(value = "/submission/problem/submitcount")
    Integer getProblemSubmitCount(@RequestParam("problemId") Long problemId);
    // memberId로 틀린 문제 조회
    @GetMapping("/submission/problem/incorrect")
    List<Long> getIncorrectProblemIds(@RequestParam Long memberId);
    // memberId로 맞춘 문제 조회
    @GetMapping("/submission/problem/correct")
    List<Long> getCorrectProblemIds(@RequestParam Long memberId);

    // problemId로 제출 정보 조회
    @GetMapping("/submission/exam/result")
    SubmissionExamResultInfoResponseDto fetchSubmissionsInfo(@RequestParam("problemId") List<Long> problemIds, @RequestParam("memberId") Long memberId);
    // 오답률 높은 문제 TOP 5
    @GetMapping("/submission/problem/incorrect/top5")
    List<CorrectRateResponseDto> getTop5IncorrectProblemList();

    // 오답률 높은 문제 분류 TOP 3를 가져오기 위한 문제 ID, 문제당 제출 개수, 오답 개수 조회
    @GetMapping("/submission/problem/incorrect/class")
    List<SubmissionResultCountResponseDto> getIncorrectProblemClass();
    // memberId와 problemId로 제출 정보 조회
    @GetMapping("/submission/recent/problem")
    Long getRecentSubmissionId(@RequestParam("problemId") Long problemId,@RequestParam("memberId") Long memberId);
}



