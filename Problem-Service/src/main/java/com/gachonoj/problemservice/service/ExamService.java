package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.common.codes.ErrorCode;
import com.gachonoj.problemservice.domain.constant.*;
import com.gachonoj.problemservice.domain.dto.request.ExamRequestDto;
import com.gachonoj.problemservice.domain.dto.request.TestcaseRequestDto;
import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.*;
import com.gachonoj.problemservice.domain.entity.*;
import com.gachonoj.problemservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.problemservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.problemservice.feign.dto.response.ProblemMemberInfoResponseDto;
import com.gachonoj.problemservice.feign.dto.response.SubmissionExamResultInfoResponseDto;
import com.gachonoj.problemservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.cookie.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ProblemRepository problemRepository;
    private final TestRepository testRepository;
    private final MemberServiceFeignClient memberServiceFeignClient;
    private final SubmissionServiceFeignClient submissionServiceFeignClient;

    private static final int PAGE_SIZE = 10;

    // 스케쥴링으로 시험 상태 변경
    @Scheduled(cron = "0 */5 * * * *") // 매 5분마다 실행
    @Transactional
    @Async
    public void updateExamStatusBasedOnCurrentTime() {
        // WRITING 상태를 제외하고 exam 가져오기
        List<Exam> exams = examRepository.findByExamStatusNot(ExamStatus.WRITING);
        LocalDateTime now = LocalDateTime.now();
        for(Exam exam : exams) {
            if (exam.getExamStartDate().isBefore(now) && exam.getExamEndDate().isAfter(now)) {
                exam.setExamStatus(ExamStatus.ONGOING);
            } else if (exam.getExamEndDate().isBefore(now)) {
                exam.setExamStatus(ExamStatus.TERMINATED);
            }
        }
    }

    // 시험 문제 등록
    @Transactional
    public void registerExam(ExamRequestDto request, Long memberId) {
        // 시간 변환 YYYY.MM.DD.HH.MM.SS -> LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        LocalDateTime startDate = LocalDateTime.parse(request.getExamStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getExamEndDate(), formatter);

        Exam exam = new Exam();  // 실제 엔티티 클래스
        exam.setExamTitle(request.getExamTitle());
        exam.setMemberId(memberId);
        exam.setExamMemo(request.getExamMemo());
        exam.setExamContents(request.getExamContents());
        exam.setExamNotice(request.getExamNotice());
        exam.setExamStartDate(startDate);
        exam.setExamEndDate(endDate);
        exam.setExamDueTime(request.getExamDueTime());
        exam.setExamStatus(request.getExamStatus());
        exam.setExamType(request.getExamType());

        examRepository.save(exam);  // 시험 정보 저장

        int questionSequence = 1;

        for (ProblemRequestDto problemRequestDto : request.getTests()) {
            Problem problem = new Problem();  // 실제 엔티티 클래스
            problem.setProblemTitle(problemRequestDto.getProblemTitle());
            problem.setProblemContents(problemRequestDto.getProblemContents());
            problem.setProblemInputContents(problemRequestDto.getProblemInputContents());
            problem.setProblemOutputContents(problemRequestDto.getProblemOutputContents());
            problem.setProblemClass(ProblemClass.valueOf(problemRequestDto.getProblemClass()));
            problem.setProblemTimeLimit(problemRequestDto.getProblemTimeLimit());
            problem.setProblemMemoryLimit(problemRequestDto.getProblemMemoryLimit());
            problem.setProblemStatus(ProblemStatus.PRIVATE);
            problem.setProblemPrompt(problemRequestDto.getProblemPrompt());

            List<Testcase> testcases = new ArrayList<>();
            for (TestcaseRequestDto testcaseDto : problemRequestDto.getTestcases()) {
                Testcase testcase = new Testcase();  // 실제 엔티티 클래스
                testcase.setTestcaseInput(testcaseDto.getTestcaseInput());
                testcase.setTestcaseOutput(testcaseDto.getTestcaseOutput());
                testcase.setTestcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()));
                testcase.setProblem(problem);
                testcases.add(testcase);
            }
            problem.setTestcases(testcases);

            problemRepository.save(problem);  // 문제 정보 저장


            // Question 엔티티 생성 및 저장
            Question question = new Question();
            question.setExam(exam);
            question.setProblem(problem);
            Integer questionScore = problemRequestDto.getQuestionScore();
            if (questionScore == null) {
                questionScore = 10;  // 기본 점수로 10 설정
            }
            question.setQuestionScore(questionScore);
            question.setQuestionSequence(questionSequence++);
            questionRepository.save(question);
        }
        // 각 후보자에 대한 테스트 엔터티 생성 및 저장
        for (Long candidateId : request.getCandidateList()) {
            Test test = new Test();
            test.setExam(exam);
            test.setMemberId(candidateId);
            testRepository.save(test);  // 테스트 정보 저장
        }
    }

    // 시험 문제 수정
    @Transactional
    public void updateExam(Long examId, ExamRequestDto request) {
        if (examId == null) {
            throw new IllegalArgumentException("Exam ID must not be null");
        }

        Exam existingExam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        existingExam.setExamTitle(request.getExamTitle());
        existingExam.setExamMemo(request.getExamMemo());
        existingExam.setExamNotice(request.getExamNotice());
        existingExam.setExamContents(request.getExamContents());
        existingExam.setExamStartDate(LocalDateTime.parse(request.getExamStartDate()));
        existingExam.setExamEndDate(LocalDateTime.parse(request.getExamEndDate()));
        existingExam.setExamDueTime(request.getExamDueTime());
        existingExam.setExamType(request.getExamType());
        examRepository.save(existingExam);

        if (request.getTests() == null || request.getTests().isEmpty()) {
            throw new IllegalArgumentException("No problems provided for the exam.");
        }

        request.getTests().forEach(dto -> {
            if (dto.getProblemId() == null) {
                throw new IllegalArgumentException("Problem ID must not be null for problem title: " + dto.getProblemTitle());
            }
        });

        updateQuestions(existingExam, request.getTests());
        updateCandidateTests(existingExam, request.getCandidateList());

        for (ProblemRequestDto problemRequestDto : request.getTests()) {
            Problem problem = problemRepository.findById(problemRequestDto.getProblemId())
                    .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemRequestDto.getProblemId()));



            problem.setProblemTitle(problemRequestDto.getProblemTitle());
            problem.setProblemContents(problemRequestDto.getProblemContents());
            problem.setProblemInputContents(problemRequestDto.getProblemInputContents());
            problem.setProblemOutputContents(problemRequestDto.getProblemOutputContents());
            problem.setProblemClass(ProblemClass.valueOf(problemRequestDto.getProblemClass()));
            problem.setProblemTimeLimit(problemRequestDto.getProblemTimeLimit());
            problem.setProblemMemoryLimit(problemRequestDto.getProblemMemoryLimit());
            problem.setProblemStatus(ProblemStatus.PRIVATE);
            problem.setProblemPrompt(problemRequestDto.getProblemPrompt());

            // 테스트 케이스 업데이트
            problem.getTestcases().clear();
            for (TestcaseRequestDto testcaseDto : problemRequestDto.getTestcases()) {
                Testcase testcase = new Testcase();
                testcase.setTestcaseInput(testcaseDto.getTestcaseInput());
                testcase.setTestcaseOutput(testcaseDto.getTestcaseOutput());
                testcase.setTestcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()));
                testcase.setProblem(problem);
                problem.getTestcases().add(testcase);
            }

            problemRepository.save(problem);
        }
    }

    // 그 안의 문제들 수정
    private void updateQuestions(Exam exam, List<ProblemRequestDto> problemRequestDtos) {
        List<Question> existingQuestions = questionRepository.findByExamExamId(exam.getExamId());
        Map<Long, Question> questionMap = existingQuestions.stream()
                .collect(Collectors.toMap(q -> q.getProblem().getProblemId(), Function.identity()));

        for (ProblemRequestDto dto : problemRequestDtos) {
            Question question = questionMap.getOrDefault(dto.getProblemId(), new Question());
            question.setExam(exam);
            Problem problem = problemRepository.findById(dto.getProblemId())
                    .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + dto.getProblemId()));
            question.setProblem(problem);
            question.setQuestionScore(dto.getQuestionScore() != null ? dto.getQuestionScore() : 10);
            question.setQuestionSequence(dto.getQuestionSequence());
            questionRepository.save(question);
        }

        existingQuestions.removeIf(q -> !problemRequestDtos.stream().map(ProblemRequestDto::getProblemId).collect(Collectors.toSet()).contains(q.getProblem().getProblemId()));
        existingQuestions.forEach(questionRepository::delete);
    }
    // 응시자 리스트 수정
    private void updateCandidateTests(Exam exam, List<Long> candidateIds) {
        List<Test> existingTests = testRepository.findByExamExamId(exam.getExamId());
        Map<Long, Test> existingTestsMap = existingTests.stream()
                .collect(Collectors.toMap(Test::getMemberId, test -> test));

        // Add or update existing candidate tests
        candidateIds.forEach(candidateId -> {
            Test test = existingTestsMap.getOrDefault(candidateId, new Test());
            test.setExam(exam);
            test.setMemberId(candidateId);
            testRepository.save(test);
        });

        // Remove tests for candidates not listed anymore
        existingTests.forEach(test -> {
            if (!candidateIds.contains(test.getMemberId())) {
                testRepository.delete(test);
            }
        });
    }

    // 시험 문제 조회
    @Transactional(readOnly = true)
    public ExamDetailResponseDto getExamDetail(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        // 문제 목록 가져오기
        List<Problem> problems = questionRepository.findProblemsByExamId(examId);
        List<ProblemDetailResponseDto> problemDetails = problems.stream()
                .map(problem -> {
                    List<String> inputs = problem.getTestcases().stream()
                            .map(Testcase::getTestcaseInput)
                            .collect(Collectors.toList());
                    List<String> outputs = problem.getTestcases().stream()
                            .map(Testcase::getTestcaseOutput)
                            .collect(Collectors.toList());
                    return new ProblemDetailResponseDto(
                            problem.getProblemTitle(),
                            problem.getProblemContents(),
                            problem.getProblemInputContents(),
                            problem.getProblemOutputContents(),
                            inputs,
                            outputs
                    );
                })
                .collect(Collectors.toList());

        return new ExamDetailResponseDto(
                exam.getExamId(),
                exam.getExamTitle(),
                exam.getExamContents(),
                formatter.format(exam.getExamStartDate()),
                formatter.format(exam.getExamEndDate()),
                exam.getExamStatus().name(),
                exam.getExamType().name(),
                exam.getExamMemo(),
                exam.getExamNotice(),
                problemDetails
        );
    }

    // 시험 삭제
    @Transactional
    public void deleteExam(Long examId, Long requestingMemberId) {
        int affectedRows = examRepository.deleteByIdAndMemberId(examId, requestingMemberId);
        if (affectedRows == 0) {
            throw new RuntimeException("No exam found or unauthorized to delete this exam");
        }
    }

    // 시험 삭제 admin
    @Transactional
    public void deleteExamByAdmin(Long examId) {
        examRepository.deleteById(examId);
    }
    // 시험 목록 조회 & 대회 목록 조회
    @Transactional(readOnly = true)
    public List<ExamCardInfoResponseDto> getExamList(Long memberId, String type, String status) {
        ExamType examType = ExamType.fromLabel(type);
        ExamStatus examStatus = ExamStatus.fromLabel(status);
        return getContests(memberId, examType, examStatus);
    }
    // 시험 목록 조회 & 대회 목록 조회
    private List<ExamCardInfoResponseDto> getContests(Long memberId, ExamType examType, ExamStatus status) {
        List<Long> examIds = new ArrayList<>();
        List<Test> tests = testRepository.findByMemberId(memberId);
        for (Test test : tests) {
            if (test.getExam().getExamStatus() == status && test.getExam().getExamType() == examType) {
                examIds.add(test.getExam().getExamId());
            }
        }
        return examIds.stream()
                .map(examId -> {
                    Exam exam = examRepository.findById(examId)
                            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));
                    String memberNickname = memberServiceFeignClient.getNicknames(exam.getMemberId());
                    return new ExamCardInfoResponseDto(exam.getExamId(),exam.getExamTitle(),memberNickname,dateFormatter(exam.getExamStartDate()),dateFormatter(exam.getExamEndDate()),exam.getExamStatus().getLabel());
                })
                .toList();
    }

    // 교수님 시험 목록 조회
    public Page<ProfessorExamListResponseDto> getProfessorExamList(Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "examUpdateDate"));
        Page<Exam> exams = examRepository.findByMemberId(memberId, pageable);
        return exams.map(exam -> {
            String examUpdateDate = dateFormatter(exam.getExamUpdateDate());
            String examCreatedDate = dateFormatter(exam.getExamCreatedDate());
            String examStartDate = dateFormatter(exam.getExamStartDate());
            return new ProfessorExamListResponseDto(exam, examUpdateDate, examCreatedDate, examStartDate);
        });
    }
    // 관리자 시험 또는 대회 목록 조회
    public Page<ExamOrContestListResponseDto> getExamOrContestList(int pageNo, String type) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "examId"));
        ExamType examType = ExamType.fromLabel(type);

        Page<Exam> exams = examRepository.findByExamType(examType, pageable);
        return exams.map(exam -> {
            String memberNickname = memberServiceFeignClient.getNicknames(exam.getMemberId());
            String examUpdateDate = dateFormatter(exam.getExamUpdateDate());
            String examCreatedDate = dateFormatter(exam.getExamCreatedDate());
            return new ExamOrContestListResponseDto(exam, examUpdateDate, examCreatedDate,memberNickname);
        });
    }

    // 시험 대기 화면 조회
    @Transactional(readOnly = true)
    public ExamOrContestInfoResponseDto getExamOrContestInfo(Long examId, String type) {
        ExamType examType = ExamType.fromLabel(type);
        if (examType == null) {
            throw new IllegalArgumentException("Invalid exam type provided.");
        }

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        // 현재 시간과 시험 시작 시간 비교
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getExamStartDate())) {
            throw new IllegalStateException("지금은 참가할 수 없습니다.");
        }

        String memberNickname = memberServiceFeignClient.getNicknames(exam.getMemberId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String formattedStartDate = exam.getExamStartDate().format(dateTimeFormatter);
        String formattedEndDate = exam.getExamEndDate().format(dateTimeFormatter);

        return new ExamOrContestInfoResponseDto(
                exam.getExamId(),
                exam.getExamTitle(),
                memberNickname,
                exam.getExamContents(),
                formattedStartDate,
                formattedEndDate,
                examType.getLabel(),
                exam.getExamNotice()
        );
    }

    // 시험 결과 조회
    @Transactional(readOnly = true)
    public ExamResultDetailsResponseDto getExamResults(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with id: " + testId));

        Exam exam = test.getExam();
        ProblemMemberInfoResponseDto memberInfo = memberServiceFeignClient.getMemberInfo(test.getMemberId());

        List<Question> questionsList = questionRepository.findByExamExamId(exam.getExamId());
        List<Long> problemIds = questionsList.stream()
                .map(question -> question.getProblem().getProblemId())
                .collect(Collectors.toList());

        SubmissionExamResultInfoResponseDto submissionsInfo = submissionServiceFeignClient.fetchSubmissionsInfo(problemIds, test.getMemberId());
        Map<Long, Question> questionMap = questionsList.stream()
                .collect(Collectors.toMap(question -> question.getProblem().getProblemId(), Function.identity()));

        final int[] totalScore = {0};
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        List<QuestionResultDetailsResponseDto> questionDtos = submissionsInfo.getSubmissions().stream()
                .map(submission -> {
                    Question question = questionMap.get(submission.getProblemId());
                    int questionScore = submission.isCorrect() ? question.getQuestionScore() : 0;
                    totalScore[0] += questionScore;
                    return new QuestionResultDetailsResponseDto(
                            question.getQuestionSequence(),
                            question.getQuestionScore(),
                            submission.getProblemId(),
                            question.getProblem().getProblemTitle(),
                            question.getProblem().getProblemContents(),
                            submission.isCorrect(),
                            submission.getSubmissionCode()
                    );
                })
                .collect(Collectors.toList());

        test.setTestScore(totalScore[0]);
        testRepository.save(test);

        String testDueTime = Duration.between(test.getTestStartDate(), test.getTestEndDate()).toHoursPart() + ":"
                + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toMinutesPart() + ":"
                + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toSecondsPart();
        String submissionDate = test.getTestEndDate().format(dateTimeFormatter);

        return new ExamResultDetailsResponseDto(
                exam.getExamTitle(),
                exam.getExamMemo(),
                testRepository.countByExamExamId(exam.getExamId()),
                memberInfo.getMemberName(),
                memberInfo.getMemberNumber(),
                memberInfo.getMemberEmail(),
                test.getTestScore(),
                testDueTime,
                submissionDate,
                questionDtos
        );
    }

    // 시험 결과 목록 조회
    @Transactional(readOnly = true)
    public Page<ExamResultListDto> getExamResultList(Long examId, Long memberId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "testEndDate"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        Page<Test> tests = testRepository.findByExamExamId(examId, pageable);
        int submissionTotal = (int) tests.getTotalElements();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        return tests.map(test -> {
            ProblemMemberInfoResponseDto memberInfo = memberServiceFeignClient.getMemberInfo(test.getMemberId());
            int totalScore = test.getTestScore();
            String testDueTime = Duration.between(test.getTestStartDate(), test.getTestEndDate()).toHoursPart() + ":"
                    + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toMinutesPart() + ":"
                    + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toSecondsPart();
            String submissionDate = test.getTestEndDate().format(dateTimeFormatter);

            return new ExamResultListDto(
                    exam.getExamTitle(),
                    exam.getExamMemo(),
                    submissionTotal,
                    test.getMemberId(),
                    memberInfo.getMemberName(),
                    memberInfo.getMemberNumber(),
                    memberInfo.getMemberEmail(),
                    totalScore,
                    testDueTime,
                    submissionDate
            );
        });
    }
    // DateFormatter를 사용하여 날짜 형식을 변경하는 메서드
    private String dateFormatter (LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
    // 교수 대시보드 진행중인 시험 목록 조회
    public List<ExamCardInfoResponseDto> getProfessorExamCardInfo(Long memberId) {
        List<Exam> exams = examRepository.findByExamStatusAndMemberId(ExamStatus.ONGOING, memberId);
        return exams.stream()
                .map(exam -> {
                    String examStartDate = dateFormatter(exam.getExamStartDate());
                    String examEndDate = dateFormatter(exam.getExamEndDate());
                    return new ExamCardInfoResponseDto(
                            exam.getExamId(),
                            exam.getExamTitle(),
                            examStartDate,
                            examEndDate,
                            exam.getExamStatus().getLabel()
                    );
                })
                .collect(Collectors.toList());
    }
    // 시험 응시자 인지 확인 & 시험 시간 맞는지 확인 하기 위한 API
    @Transactional
    public ExamEnterResponseDto checkExamEnter(Long examId, Long memberId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));
        if (exam.getExamStatus() == ExamStatus.ONGOING) {
            Test test = testRepository.findByExamExamIdAndMemberId(examId, memberId);
            // 응시 시작 시간 TestStartDate에 저장
            if (test.getTestStartDate() == null) {
                test.setTestStartDate(LocalDateTime.now());
                testRepository.save(test);
            }
            if (test == null) {
                throw new IllegalArgumentException("You are not allowed to enter this exam.");
            }
            return new ExamEnterResponseDto(true);
        } else {
            throw new IllegalArgumentException("This exam is not ongoing.");
        }
    }
}
