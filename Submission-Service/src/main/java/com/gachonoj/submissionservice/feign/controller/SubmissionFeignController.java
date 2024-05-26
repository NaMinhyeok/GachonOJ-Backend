package com.gachonoj.submissionservice.feign.controller;

import com.gachonoj.submissionservice.feign.dto.response.CorrectRateResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionResultCountResponseDto;
import com.gachonoj.submissionservice.feign.service.SubmissionFeignService;
import com.gachonoj.submissionservice.fegin.dto.response.SubmissionCodeInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionFeignController {
    private final SubmissionFeignService submissionFeignService;

    // 사용자 푼 문제, 시도한 문제 수 조회
    @GetMapping("/member/info")
    public SubmissionMemberInfoResponseDto getMemberInfo(Long memberId) {
        return submissionFeignService.getMemberInfo(memberId);
    }
    // 사용자 푼 문제 수 조회
    @GetMapping("/member/solved")
    public Integer getMemberSolved(Long memberId) {
        return submissionFeignService.getMemberSolved(memberId);
    }

    // 정답자 제출 인원 조회
    @GetMapping("/problem/people")
    public Integer getCorrectSubmission(Long problemId) {
        return submissionFeignService.getCorrectSubmission(problemId);
    }

    // 정답률 조회
    @GetMapping("/problem/rate")
    public double getProblemCorrectRate(Long problemId) {
        return submissionFeignService.getProblemCorrectRate(problemId);
    }

    // 틀린 문제 조회
    @GetMapping("problem/incorrect")
    public List<Long> getIncorrectProblemIds(@RequestParam Long memberId) {
        return submissionFeignService.getIncorrectProblemIdsByMemberId(memberId);
    }

    // 맞춘 문제 조회
    @GetMapping("problem/correct")
    public List<Long> getCorrectProblemIds(@RequestParam Long memberId) {
        return submissionFeignService.getCorrectProblemIdsByMemberId(memberId);
    }
    // 총 제출 수 조회
    @GetMapping("/problem/submitcount")
    public Integer getProblemSubmitCount(Long problemId) {
        return submissionFeignService.getProblemSubmitCount(problemId);
    }

    // 제출 번호 통해서 제출 코드, 문제 ID 가져오기
    @GetMapping("/code/{submissionId}")
    public SubmissionCodeInfoResponseDto getSubmissionCodeBySubmissionId(@PathVariable Long submissionId) {
        return submissionFeignService.getSubmissionCodeInfo(submissionId);
    }
    // 오답률 높은 문제 TOP 5
    @GetMapping("/problem/incorrect/top5")
    public List<CorrectRateResponseDto> getTop5IncorrectProblemList() {
        return submissionFeignService.getTop5IncorrectProblemList();
    }
    //    // 오답률 높은 문제 분류 TOP 3를 가져오기 위한 문제 ID, 문제당 제출 개수, 오답 개수 조회
    @GetMapping("/submission/problem/incorrect/class")
    public List<SubmissionResultCountResponseDto> getIncorrectProblemClass() {
        return submissionFeignService.getIncorrectProblemClass();
    }
    // memberId와 problemId로 제출 정보 조회
    @GetMapping("/recent/problem")
    public Long getRecentSubmissionId(@RequestParam("memberId") Long memberId, @RequestParam("problemId") Long problemId) {
        return submissionFeignService.getRecentSubmissionId(memberId, problemId);
    }
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @DeleteMapping("/member")
    public void deleteSubmissionByMemberId(@RequestParam("memberId") Long memberId) {
        submissionFeignService.deleteSubmissionByMemberId(memberId);
    }
    // 시험 삭제 시 해당 시험에 대한 제출 삭제
    @DeleteMapping("/exam")
    public void deleteSubmissionByProblemIds(@RequestParam("problemIds") List<Long> problemIds) {
        submissionFeignService.deleteSubmissionByProblemIds(problemIds);
    }
    // 문제에 대한 제출 삭제
    @DeleteMapping("/problem")
    public void deleteSubmissionByProblemId(@RequestParam("problemId") Long problemId) {
        submissionFeignService.deleteSubmissionByProblemId(problemId);
    }
}
