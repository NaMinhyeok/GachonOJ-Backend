package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.*;
import com.gachonoj.problemservice.domain.entity.Bookmark;
import com.gachonoj.problemservice.domain.entity.Exam;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Testcase;
import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.problemservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.problemservice.repository.BookmarkRepository;
import com.gachonoj.problemservice.repository.ExamRepository;
import com.gachonoj.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ExamRepository examRepository;
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
        Pageable pageable = PageRequest.of(pageNo-1, 10, Sort.by(Sort.Direction.DESC, "problem.problemId")); // 정렬 기준 수정
        Page<Bookmark> bookmarks = bookmarkRepository.findByMemberId(memberId, pageable);

        return bookmarks.map(bookmark -> {
            Problem problem = bookmark.getProblem(); // 문제 조회를 위해 필요없는 호출 제거
            if (problem == null) {
                throw new IllegalArgumentException("Problem not found");
            }
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new BookmarkProblemResponseDto(
                    problem.getProblemId(),
                    problem.getProblemTitle(),
                    problem.getProblemDiff(),
                    problem.getProblemClass(),
                    correctPeople,
                    correctRate,
                    true
            );
        });
    }

    // 북마크 기능 구현
    @Transactional
    public void addBookmark(Long memberId, Long problemId) {
        if (!bookmarkRepository.existsByMemberIdAndProblemProblemId(memberId, problemId)) {
            Problem problem = problemRepository.findByProblemId(problemId)
                    .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemId));
            Bookmark bookmark = new Bookmark(memberId, problem);
            bookmarkRepository.save(bookmark);
        } else {
            throw new IllegalStateException("Bookmark already exists");
        }
    }

    @Transactional(readOnly = true)
    public Page<WrongProblemResponseDto> getIncorrectProblemList(Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by(Sort.Direction.DESC, "problemId"));

        List<Long> problemIds = submissionServiceFeignClient.getIncorrectProblemIds(memberId);

        // 페이지네이션 적용한 문제 ID 리스트 조회
        Page<Problem> problems = problemRepository.findAllByProblemIdIn(problemIds, pageable);

        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new WrongProblemResponseDto(
                    problem.getProblemId(),
                    problem.getProblemTitle(),
                    problem.getProblemDiff(),
                    problem.getProblemClass(),
                    correctPeople,
                    correctRate,
                    false // isBookmarked 필드는 사용자가 별도로 제공해야 하는 정보를 기반으로 설정합니다
            );
        });
    }

    @Transactional(readOnly = true)
    public Page<SolvedProblemResponseDto> getSolvedProblemList(Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by(Sort.Direction.DESC, "problemId"));

        List<Long> problemIds = submissionServiceFeignClient.getCorrectProblemIds(memberId);

        // 페이지네이션 적용한 문제 ID 리스트 조회
        Page<Problem> problems = problemRepository.findAllByProblemIdIn(problemIds, pageable);

        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new SolvedProblemResponseDto(
                    problem.getProblemId(),
                    problem.getProblemTitle(),
                    problem.getProblemDiff(),
                    problem.getProblemClass(),
                    correctPeople,
                    correctRate,
                    false // isBookmarked 필드는 사용자가 별도로 제공해야 하는 정보를 기반으로 설정합니다
            );
        });
    }
    // 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListResponseDto> getProblemList(int pageNo, String search, String classType,Integer diff, String sortType) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        if(search!= null) {
            Page<Problem> problems = problemRepository.findByProblemTitleContaining(search, pageable);
            return problems.map(problem -> {
                Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
                Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
                return new ProblemListResponseDto(problem, correctPeople, correctRate);
            });
        } else if (classType != null) {
            ProblemClass problemClass = ProblemClass.fromLabel(classType);
            Page<Problem> problems = problemRepository.findByProblemClass(problemClass, pageable);

            return problems.map(problem -> {
                Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
                Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
                return new ProblemListResponseDto(problem, correctPeople, correctRate);
            });
        } else if (diff != null) {
            Page<Problem> problems = problemRepository.findByProblemDiff(diff, pageable);
            return problems.map(problem -> {
                Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
                Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
                return new ProblemListResponseDto(problem, correctPeople, correctRate);
            });
        } else if (sortType != null) {
            if (sortType.equals("DESC")) {
                pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemDiff"));
                Page<Problem> problems = problemRepository.findAll(pageable);
                return problems.map(problem -> {
                    Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
                    Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
                    return new ProblemListResponseDto(problem, correctPeople, correctRate);
                });
            } else if(sortType.equals("ASC")) {
                pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "problemDiff"));
                Page<Problem> problems = problemRepository.findAll(pageable);
                return problems.map(problem -> {
                    Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
                    Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
                    return new ProblemListResponseDto(problem, correctPeople, correctRate);
                });
            }
        }
        Page<Problem> problems = problemRepository.findAll(pageable);
        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new ProblemListResponseDto(problem, correctPeople, correctRate);
        });
    }
    // 추천 알고리즘 문제 조회
    public List<RecommendProblemResponseDto> getRecommenedProblemList() {
        List<Problem> problem = problemRepository.findTop6ByOrderByProblemCreatedDateDesc();
        return problem.stream().map(RecommendProblemResponseDto::new).collect(Collectors.toList());
    }
}