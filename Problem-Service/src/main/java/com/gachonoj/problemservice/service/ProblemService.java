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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
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
//    @Transactional(readOnly = true)
//    public Page<BookmarkProblemResponseDto> getBookmarkProblemList(Long memberId, int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo-1, 10, Sort.by(Sort.Direction.DESC, "problem.problemId")); // 정렬 기준 수정
//        Page<Bookmark> bookmarks = bookmarkRepository.findByMemberId(memberId, pageable);
//
//        return bookmarks.map(bookmark -> {
//            Problem problem = bookmark.getProblem(); // 문제 조회를 위해 필요없는 호출 제거
//            if (problem == null) {
//                throw new IllegalArgumentException("Problem not found");
//            }
//            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
//            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
//            return new BookmarkProblemResponseDto(
//                    problem.getProblemId(),
//                    problem.getProblemTitle(),
//                    problem.getProblemDiff(),
//                    problem.getProblemClass(),
//                    correctPeople,
//                    correctRate,
//                    true
//            );
//        });
//    }

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

//    @Transactional(readOnly = true)
//    public Page<WrongProblemResponseDto> getIncorrectProblemList(Long memberId, int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by(Sort.Direction.DESC, "problemId"));
//
//        List<Long> problemIds = submissionServiceFeignClient.getIncorrectProblemIds(memberId);
//
//        // 페이지네이션 적용한 문제 ID 리스트 조회
//        Page<Problem> problems = problemRepository.findAllByProblemIdIn(problemIds, pageable);
//
//        return problems.map(problem -> {
//            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
//            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
//            return new WrongProblemResponseDto(
//                    problem.getProblemId(),
//                    problem.getProblemTitle(),
//                    problem.getProblemDiff(),
//                    problem.getProblemClass(),
//                    correctPeople,
//                    correctRate,
//                    false // isBookmarked 필드는 사용자가 별도로 제공해야 하는 정보를 기반으로 설정합니다
//            );
//        });
//    }
//
//    @Transactional(readOnly = true)
//    public Page<SolvedProblemResponseDto> getSolvedProblemList(Long memberId, int pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by(Sort.Direction.DESC, "problemId"));
//
//        List<Long> problemIds = submissionServiceFeignClient.getCorrectProblemIds(memberId);
//
//        // 페이지네이션 적용한 문제 ID 리스트 조회
//        Page<Problem> problems = problemRepository.findAllByProblemIdIn(problemIds, pageable);
//
//        return problems.map(problem -> {
//            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
//            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
//            return new SolvedProblemResponseDto(
//                    problem.getProblemId(),
//                    problem.getProblemTitle(),
//                    problem.getProblemDiff(),
//                    problem.getProblemClass(),
//                    correctPeople,
//                    correctRate,
//                    false // isBookmarked 필드는 사용자가 별도로 제공해야 하는 정보를 기반으로 설정합니다
//            );
//        });
//    }
    //TODO : 검색, 분류, 난이도, 정렬 기능 추가
    // 사용자 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListResponseDto> getProblemListByMember(String type, int pageNo, String search, String classType,Integer diff,String sortType,Long memberId) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        return switch (type) {
            case "bookmark" -> getBookmarkProblemList(memberId, pageable);
            case "solved" -> getSolvedProblemList(memberId, pageable);
            case "wrong" -> getWrongProblemList(memberId, pageable);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }
    // 북마크 문제 조회 메서드
    private Page<ProblemListResponseDto> getBookmarkProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = bookmarkRepository.findByMemberId(memberId).stream()
                .map(bookmark -> bookmark.getProblem().getProblemId())
                .collect(Collectors.toList());
        return getProblemListResponseDtoPage(problemIds, pageable);

    }
    // 맞은 문제 조회 메서드
    private Page<ProblemListResponseDto> getSolvedProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = submissionServiceFeignClient.getCorrectProblemIds(memberId);
        return getProblemListResponseDtoPage(problemIds, pageable);
    }
    // 틀린 문제 조회 메서드
    private Page<ProblemListResponseDto> getWrongProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = submissionServiceFeignClient.getIncorrectProblemIds(memberId);
        return getProblemListResponseDtoPage(problemIds, pageable);
    }
    // 문제 목록 조회 메서드
    private Page<ProblemListResponseDto> getProblemListResponseDtoPage(List<Long> problemIds, Pageable pageable) {
        Page<Problem> problems = problemRepository.findAllByProblemIdIn(problemIds, pageable);
        return problems.map(this::createProblemListResponseDto);
    }
    // DTO 생성 메서드
    private ProblemListResponseDto createProblemListResponseDto(Problem problem) {
        Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
        Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
        return new ProblemListResponseDto(problem, correctPeople, correctRate);
    }

    // 비로그인 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListResponseDto> getProblemList(int pageNo, String search, String classType, Integer diff, String sortType) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));

        switch (sortType) {
            case "DESC":
                pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemDiff"));
                break;
            case "ASC":
                pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "problemDiff"));
                break;
        }

        Page<Problem> problems;
        if (search != null) {
            problems = problemRepository.findByProblemTitleContaining(search, pageable);
        } else if (classType != null) {
            ProblemClass problemClass = ProblemClass.fromLabel(classType);
            problems = problemRepository.findByProblemClass(problemClass, pageable);
        } else if (diff != null) {
            problems = problemRepository.findByProblemDiff(diff, pageable);
        } else {
            problems = problemRepository.findAll(pageable);
        }

        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            return new ProblemListResponseDto(problem, correctPeople, correctRate);
        });
    }
    // 추천 알고리즘 문제 조회
    @Transactional(readOnly = true)
    public List<RecommendProblemResponseDto> getRecommenedProblemList() {
        List<Problem> problem = problemRepository.findTop6ByOrderByProblemCreatedDateDesc();
        return problem.stream().map(RecommendProblemResponseDto::new).collect(Collectors.toList());
    }
    // 관리자 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListByAdminResponseDto> getProblemListByAdmin(int pageNo,String search) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        Page<Problem> problems;
        if(search != null){
            problems = problemRepository.findByProblemTitleContaining(search, pageable);
        } else{
            problems = problemRepository.findAll(pageable);

        }
        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Integer correctSumbit = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Integer sumbitCount = submissionServiceFeignClient.getProblemSubmitCount(problem.getProblemId());
            String problemCreatedDate = dateFormatter(problem.getProblemCreatedDate());
            String problemStatus = problem.getProblemStatus().getLabel();
            return new ProblemListByAdminResponseDto(problem, correctPeople, correctSumbit, sumbitCount, problemCreatedDate,problemStatus);
        });
    }
    //문제 응시 화면 문제 상세 조회
    @Transactional(readOnly = true)
    public ProblemDetailResponseDto getProblemDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemId));
        List<Testcase> visibleTestcases = problem.getTestcases().stream()
                .filter(testcase -> testcase.getTestcaseStatus() == TestcaseStatus.VISIBLE)
                .toList();
        List<String> testcaseInputs = visibleTestcases.stream()
                .map(Testcase::getTestcaseInput)
                .collect(Collectors.toList());
        List<String> testcaseOutputs = visibleTestcases.stream()
                .map(Testcase::getTestcaseOutput)
                .collect(Collectors.toList());
        return new ProblemDetailResponseDto(problem, testcaseInputs, testcaseOutputs);
    }
    // DateFormatter를 사용하여 날짜 형식을 변경하는 메서드
    private String dateFormatter (LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}