package com.gachonoj.problemservice.feign.service;

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
}
