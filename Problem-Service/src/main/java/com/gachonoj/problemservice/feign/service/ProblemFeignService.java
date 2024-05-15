package com.gachonoj.problemservice.feign.service;

import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Testcase;
import com.gachonoj.problemservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import com.gachonoj.problemservice.repository.ProblemRepository;
import com.gachonoj.problemservice.repository.TestcaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemFeignService {

    private final ProblemRepository problemRepository;
    private final TestcaseRepository testcaseRepository;

    // 북마크 갯수 조회
    public Integer getBookmarkCountByMemberId(Long memberId) {
        return problemRepository.getBookmarkCountByMemberId(memberId);
    }

    // 정답자 수 조회

    // 문제의 테스트케이스 조회
    public List<SubmissionProblemTestCaseResponseDto> getTestCases(Long problemId) {
        List<Testcase> testcases = testcaseRepository.findByProblemProblemId(problemId);
        return testcases.stream()
                .map(testcase -> new SubmissionProblemTestCaseResponseDto(testcase.getTestcaseInput(), testcase.getTestcaseOutput()))
                .toList();
    }
    // 문제의 공개된 테스트케이스 조회
    public List<SubmissionProblemTestCaseResponseDto> getVisibleTestCases(Long problemId){
        List<Testcase> testcases = testcaseRepository.findByProblemProblemIdAndTestcaseStatus(problemId, TestcaseStatus.VISIBLE);
        return testcases.stream()
                .map(testcase -> new SubmissionProblemTestCaseResponseDto(testcase.getTestcaseInput(), testcase.getTestcaseOutput()))
                .toList();
    }
    // 문제 점수 조회
    public Integer getProblemScore(Long problemId){
        Problem problem = problemRepository.findByProblemId(problemId)
                .orElseThrow(()->new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        Integer problemDiff = problem.getProblemDiff();
        return calculateProblemScore(problemDiff);
    }
    // 문제 점수 계산
    public Integer calculateProblemScore(Integer problemDiff){
        return switch (problemDiff) {
            case 1 -> 5;
            case 2 -> 10;
            case 3 -> 15;
            case 4 -> 20;
            case 5 -> 25;
            default -> 0;
        };
    }
    // 문제 ID로 문제 프롬프트 가져오기
    public String getProblemPromptByProblemId(Long problemId){
        Problem problem = problemRepository.findByProblemId(problemId)
                .orElseThrow(()->new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        return problem.getProblemPrompt();
    }
    // 문제 time limit 가져오기
    public Integer getProblemTimeLimit(Long problemId){
        Problem problem = problemRepository.findByProblemId(problemId)
                .orElseThrow(()->new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        return problem.getProblemTimeLimit();
    }
    // 문제 제목 가져오기
    public String getProblemTitle(Long problemId){
        Problem problem = problemRepository.findByProblemId(problemId)
                .orElseThrow(()->new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
        return problem.getProblemTitle();
    }
}
