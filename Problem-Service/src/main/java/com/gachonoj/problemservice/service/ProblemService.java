package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.ProblemDetailAdminResponseDto;
import com.gachonoj.problemservice.domain.dto.response.*;
import com.gachonoj.problemservice.domain.entity.Bookmark;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Testcase;
import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.feign.client.AiServiceFeignClient;
import com.gachonoj.problemservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.problemservice.feign.dto.response.CorrectRateResponseDto;
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
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ExamRepository examRepository;
    private final SubmissionServiceFeignClient submissionServiceFeignClient;
    private final AiServiceFeignClient aiServiceFeignClient;


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
    public void updateProblem(Long problemId, ProblemRequestDto requestDto) {
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
        List<Testcase> testcases = problem.getTestcases();
        testcases.clear();
        requestDto.getTestcases().forEach(testcaseDto -> {
            Testcase testcase = Testcase.builder()
                    .testcaseInput(testcaseDto.getTestcaseInput())
                    .testcaseOutput(testcaseDto.getTestcaseOutput())
                    .testcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()))
                    .build();
            problem.addTestcase(testcase);
        });
    }

    // 문제 삭제
    @Transactional
    public void deleteProblem(Long problemId) {
        // 문제에 대한 제출 삭제
        submissionServiceFeignClient.deleteSubmissionByProblemId(problemId);
        // 문제에 대한 ai 분석 삭제
        aiServiceFeignClient.deleteAiByProblemId(problemId);
        // 문제가 존재하는지 확인하고, 존재한다면 삭제
        problemRepository.findById(problemId)
                .ifPresent(problemRepository::delete);
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
    // 북마크 삭제 기능 구현
    @Transactional
    public void removeBookmark(Long memberId, Long problemId) {
        // 북마크가 존재하는지 확인
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndProblemProblemId(memberId, problemId)
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found with memberId: " + memberId + " and problemId: " + problemId));

        // 북마크 삭제
        bookmarkRepository.delete(bookmark);
    }

    // 사용자 문제 목록 조회
    // TODO : type -> wrong 일 시 틀린 문제 목록 조회 구현
    @Transactional(readOnly = true)
    public Page<ProblemListResponseDto> getProblemListByMember(String type, int pageNo, String search, String classType, Integer diff, String sortType, Long memberId) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        if(sortType != null){
            pageable = switch (sortType) {
                case "DESC" -> PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemDiff"));
                case "ASC" -> PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "problemDiff"));
                default -> pageable;
            };
        }
        if (type == null) {
            return getAllProblemList(memberId, pageable,search,diff);
        }
        return switch (type) {
            case "bookmark" -> getBookmarkProblemList(memberId, pageable);
            case "solved" -> getSolvedProblemList(memberId, pageable);
            case "wrong" -> getWrongProblemList(memberId, pageable);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }
    // 전체 문제 목록 조회 메소드
    private Page<ProblemListResponseDto> getAllProblemList(Long memberId, Pageable pageable, String search,Integer diff) {
        Page<Problem> problems = (search != null)
                ? problemRepository.findByProblemTitleContainingAndProblemStatus(search, ProblemStatus.REGISTERED, pageable)
                : problemRepository.findByProblemStatus(ProblemStatus.REGISTERED, pageable);
        if(diff != null){
            problems = problemRepository.findByProblemDiffAndProblemStatus(diff, ProblemStatus.REGISTERED, pageable);
        }
        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
            Boolean isBookmarked = problem.getBookmarks().stream().anyMatch(bookmark -> bookmark.getMemberId().equals(memberId));
            return new ProblemListResponseDto(problem, correctPeople, correctRate, isBookmarked, null);
        });
    }

    // 북마크 문제 조회 메서드
    private Page<ProblemListResponseDto> getBookmarkProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = bookmarkRepository.findByMemberId(memberId).stream()
                .map(bookmark -> bookmark.getProblem().getProblemId())
                .toList();
        return getProblemListResponseDtoPage(problemIds, pageable,memberId);

    }
    // 맞은 문제 조회 메서드
    private Page<ProblemListResponseDto> getSolvedProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = submissionServiceFeignClient.getCorrectProblemIds(memberId);
        log.info("Number of problems the user has submitted answers to: {}", problemIds.size());
        for (Long problemId : problemIds) {
            log.info("Problem ID: {}", problemId);
        }
        return getProblemListResponseDtoPage(problemIds, pageable,memberId);
    }
    // 틀린 문제 조회 메서드
    private Page<ProblemListResponseDto> getWrongProblemList(Long memberId, Pageable pageable) {
        List<Long> problemIds = submissionServiceFeignClient.getIncorrectProblemIds(memberId);
        return getProblemListResponseDtoPage(problemIds, pageable,memberId);
    }

    // 문제 목록 조회 메서드
    private Page<ProblemListResponseDto> getProblemListResponseDtoPage(List<Long> problemIds, Pageable pageable,Long memberId) {
        Page<Problem> problems = problemRepository.findAllByProblemIdInAndProblemStatus(problemIds, ProblemStatus.REGISTERED, pageable);
        return problems.map(problem -> createProblemListResponseDto(problem,memberId));
    }
    // 사용자 문제 목록 DTO 생성 메서드
    private ProblemListResponseDto createProblemListResponseDto(Problem problem,Long memberId) {
        // 문제의 정답자 수
        Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
        // 문제의 정답률
        Double correctRate = submissionServiceFeignClient.getProblemCorrectRate(problem.getProblemId());
        // 북마크 여부 판단
        Boolean isBookmarked = problem.getBookmarks().stream().anyMatch(bookmark -> bookmark.getMemberId().equals(memberId));
        // 제출 ID 가져오기
        Long submissionId = submissionServiceFeignClient.getRecentSubmissionId(problem.getProblemId(),memberId);
        return new ProblemListResponseDto(problem, correctPeople, correctRate,isBookmarked,submissionId);
    }

    // 비로그인 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListResponseDto> getProblemList(int pageNo, String search, String classType, Integer diff, String sortType) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        if(sortType != null){
            pageable = switch (sortType) {
                case "DESC" -> PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemDiff"));
                case "ASC" -> PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "problemDiff"));
                default -> pageable;
            };
        }

        Page<Problem> problems;
        if (search != null) {
            problems = problemRepository.findByProblemTitleContainingAndProblemStatus(search, ProblemStatus.REGISTERED, pageable);
        } else if (classType != null) {
            ProblemClass problemClass = ProblemClass.fromLabel(classType);
            problems = problemRepository.findByProblemClassAndProblemStatus(problemClass, ProblemStatus.REGISTERED, pageable);
        } else if (diff != null) {
            problems = problemRepository.findByProblemDiffAndProblemStatus(diff, ProblemStatus.REGISTERED, pageable);
        } else {
            problems = problemRepository.findByProblemStatus(ProblemStatus.REGISTERED, pageable);
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
        List<Problem> problem = problemRepository.findTop6ByProblemStatusOrderByProblemCreatedDateDesc(ProblemStatus.REGISTERED);
        return problem.stream().map(RecommendProblemResponseDto::new).collect(Collectors.toList());
    }
    // 관리자 문제 목록 조회
    @Transactional(readOnly = true)
    public Page<ProblemListByAdminResponseDto> getProblemListByAdmin(int pageNo,String search) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "problemId"));
        Page<Problem> problems;
        if(search != null){
            problems = problemRepository.findByProblemTitleContainingAndProblemStatusNot(search,ProblemStatus.PRIVATE, pageable);
        } else{
            problems = problemRepository.findByProblemStatusNot(ProblemStatus.PRIVATE, pageable);

        }
        return problems.map(problem -> {
            Integer correctPeople = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Integer correctSubmit = submissionServiceFeignClient.getCorrectSubmission(problem.getProblemId());
            Integer submitCount = submissionServiceFeignClient.getProblemSubmitCount(problem.getProblemId());
            String problemCreatedDate = dateFormatter(problem.getProblemCreatedDate());
            String problemStatus = problem.getProblemStatus().getLabel();
            return new ProblemListByAdminResponseDto(problem, correctPeople, correctSubmit, submitCount, problemCreatedDate,problemStatus);
        });
    }
    //문제 응시 화면 문제 상세 조회
    @Transactional(readOnly = true)
    public ProblemDetailResponseDto getProblemDetail(Long problemId) {
        Problem problem = problemRepository.findProblemByProblemId(problemId)
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


    // 문제 수정시 문제 상세 조회
    @Transactional(readOnly = true)
    public ProblemDetailAdminResponseDto getProblemDetailAdmin(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem with ID " + problemId + " not found"));

        return new ProblemDetailAdminResponseDto(problem);
    }

    // DateFormatter를 사용하여 날짜 형식을 변경하는 메서드
    private String dateFormatter (LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    // 교수 대시보드 오답률 높은 알고리즘 TOP 5
    @Transactional(readOnly = true)
    public List<ProblemCardResponseDto> getTop5IncorrectProblem() {
        List<CorrectRateResponseDto> correctRateResponseDtos = submissionServiceFeignClient.getTop5IncorrectProblemList();
        return correctRateResponseDtos.stream().map(correctRateResponseDto -> {
            Problem problem = problemRepository.findById(correctRateResponseDto.getProblemId())
                    .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + correctRateResponseDto.getProblemId()));
            String problemDiff = problem.getProblemDiff() + "단계";
            String correctRate = String.format("%.2f", correctRateResponseDto.getCorrectRate()) + "%";
            return new ProblemCardResponseDto(problem, problemDiff, problem.getProblemClass().getLabel(), correctRate);
        }).toList();
    }
}