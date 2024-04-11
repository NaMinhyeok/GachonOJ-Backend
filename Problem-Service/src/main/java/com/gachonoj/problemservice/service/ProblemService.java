package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Testcase;
import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    // 문제 등록
    @Transactional
    public Long registerProblem(ProblemRequestDto requestDto) {
        Problem problem = Problem.create(
                requestDto.getProblemTitle(),
                requestDto.getProblemContents(),
                requestDto.getProblemInputContents(),
                requestDto.getProblemOutputContents(),
                requestDto.getProblemDiff(),
                requestDto.getProblemTimeLimit(),
                requestDto.getProblemMemoryLimit(),
                requestDto.getProblemPrompt(),
                ProblemClass.valueOf(requestDto.getProblemClass()),
                ProblemStatus.valueOf(requestDto.getProblemStatus())
        );

        requestDto.getTestcases().forEach(testcaseDto -> {
            Testcase testcase = Testcase.builder()
                    .testcaseInput(testcaseDto.getTestcaseInput())
                    .testcaseOutput(testcaseDto.getTestcaseOutput())
                    .testcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()))
                    .build();
            problem.addTestcase(testcase);
        });

        problemRepository.save(problem);
        return problem.getProblemId();
    }
}