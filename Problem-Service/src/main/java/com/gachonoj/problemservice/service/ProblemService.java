package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.BookmarkProblemResponseDto;
import com.gachonoj.problemservice.domain.entity.Bookmark;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Testcase;
import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.problemservice.repository.BookmarkRepository;
import com.gachonoj.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final BookmarkRepository bookmarkRepository;
    private final SubmissionServiceFeignClient submissionServiceFeignClient;

    private static final int PAGE_SIZE = 10;

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

    // 문제 수정
    @Transactional
    public Long updateProblem(Long problemId, ProblemRequestDto requestDto) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemId));

        problem.update(
                requestDto.getProblemTitle(),
                requestDto.getProblemContents(),
                requestDto.getProblemInputContents(),
                requestDto.getProblemOutputContents(),
                requestDto.getProblemDiff(),
                ProblemClass.valueOf(requestDto.getProblemClass()), // Enum 변환
                requestDto.getProblemTimeLimit(),
                requestDto.getProblemMemoryLimit(),
                ProblemStatus.valueOf(requestDto.getProblemStatus()), // Enum 변환
                requestDto.getProblemPrompt()
        );
        return problemId;
    }

    // 문제 삭제
    @Transactional
    public void deleteProblem(Long problemId) {
        // 문제가 존재하는지 확인하고, 존재한다면 삭제
        problemRepository.findById(problemId)
                .ifPresent(problemRepository::delete);
    }

    // 사용자 북마크 문제 조회

    @Transactional(readOnly = true)
    public Page<BookmarkProblemResponseDto> getBookmarkProblemList(Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1, 10, Sort.by(Sort.Direction.DESC, "problemId"));
        Page<Bookmark> bookmarks = bookmarkRepository.findByMemberId(memberId, pageable);

        return bookmarks.map(bookmark -> {
            Problem problem = problemRepository.findById(bookmark.getProblem().getProblemId())
                    .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Integer correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new BookmarkProblemResponseDto(
                    problem.getProblemTitle(),
                    problem.getProblemDiff(),
                    problem.getProblemClass(),
                    correctPeople,
                    correctRate,
                    true
            );
        });
    }
}