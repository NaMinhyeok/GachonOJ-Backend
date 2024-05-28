package com.gachonoj.problemservice.service;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final TestcaseRepository testcaseRepository;
    private final MemberServiceFeignClient memberServiceFeignClient;
    private final SubmissionServiceFeignClient submissionServiceFeignClient;

    @PersistenceContext
    private EntityManager entityManager;
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
        exam.setMemberId(memberId);
        exam.setExamTitle(request.getExamTitle());
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
    public void updateExam(Long examId, Long memberId, ExamRequestDto request) {
        if (examId == null) {
            throw new IllegalArgumentException("Exam ID must not be null");
        }

        Exam existingExam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        LocalDateTime startDate = LocalDateTime.parse(request.getExamStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getExamEndDate(), formatter);

        if (!existingExam.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Member ID does not match the exam owner.");
        }

        existingExam.setExamTitle(request.getExamTitle());
        existingExam.setExamMemo(request.getExamMemo());
        existingExam.setExamContents(request.getExamContents());
        existingExam.setExamNotice(request.getExamNotice());
        existingExam.setExamStartDate(startDate);
        existingExam.setExamEndDate(endDate);
        existingExam.setExamDueTime(request.getExamDueTime());
        existingExam.setExamStatus(request.getExamStatus());
        existingExam.setExamType(request.getExamType());

        if (request.getTests() == null || request.getTests().isEmpty()) {
            throw new IllegalArgumentException("No problems provided for the exam.");
        }

        for (ProblemRequestDto problemRequestDto : request.getTests()) {
            Problem problem;
            if (problemRequestDto.getProblemId() == null) {
                // 새로운 문제 추가
                problem = new Problem();
            } else {
                // 기존 문제 업데이트
                problem = problemRepository.findByProblemId(problemRequestDto.getProblemId())
                        .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + problemRequestDto.getProblemId()));
            }

            problem.setProblemTitle(problemRequestDto.getProblemTitle());
            problem.setProblemContents(problemRequestDto.getProblemContents());
            problem.setProblemInputContents(problemRequestDto.getProblemInputContents());
            problem.setProblemOutputContents(problemRequestDto.getProblemOutputContents());
            problem.setProblemClass(ProblemClass.valueOf(problemRequestDto.getProblemClass()));
            problem.setProblemTimeLimit(problemRequestDto.getProblemTimeLimit());
            problem.setProblemMemoryLimit(problemRequestDto.getProblemMemoryLimit());
            problem.setProblemStatus(ProblemStatus.PRIVATE);
            problem.setProblemPrompt(problemRequestDto.getProblemPrompt());

            // 기존의 테스트 케이스를 삭제하고 새로 추가
            testcaseRepository.deleteAll(problem.getTestcases());
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

        examRepository.save(existingExam);
        updateQuestions(existingExam, request.getTests());
        updateCandidateTests(existingExam, request.getCandidateList());
    }

    // 질문 업데이트 메서드
    @Transactional
    public void updateQuestions(Exam exam, List<ProblemRequestDto> problemRequestDtos) {
        List<Question> existingQuestions = questionRepository.findByExamExamId(exam.getExamId());
        Map<Long, Question> existingQuestionsMap = existingQuestions.stream()
                .collect(Collectors.toMap(q -> q.getProblem().getProblemId(), Function.identity()));

        Set<Long> updatedProblemIds = problemRequestDtos.stream()
                .map(ProblemRequestDto::getProblemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 기존 질문을 업데이트하거나 삭제
        for (Question existingQuestion : existingQuestions) {
            Long problemId = existingQuestion.getProblem().getProblemId();
            if (updatedProblemIds.contains(problemId)) {
                ProblemRequestDto dto = problemRequestDtos.stream()
                        .filter(p -> p.getProblemId().equals(problemId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Problem not found in request DTOs"));
                existingQuestion.setQuestionScore(dto.getQuestionScore() != null ? dto.getQuestionScore() : 10);
                existingQuestion.setQuestionSequence(dto.getQuestionSequence());
                questionRepository.save(existingQuestion);
            } else {
                questionRepository.delete(existingQuestion);
            }
        }

        // 새로운 질문을 삽입
        for (ProblemRequestDto dto : problemRequestDtos) {
            if (dto.getProblemId() == null || !existingQuestionsMap.containsKey(dto.getProblemId())) {
                Problem problem;
                if (dto.getProblemId() != null) {
                    problem = problemRepository.findById(dto.getProblemId())
                            .orElseThrow(() -> new IllegalArgumentException("Problem not found with id: " + dto.getProblemId()));
                } else {
                    problem = new Problem();
                }

                problem.setProblemTitle(dto.getProblemTitle());
                problem.setProblemContents(dto.getProblemContents());
                problem.setProblemInputContents(dto.getProblemInputContents());
                problem.setProblemOutputContents(dto.getProblemOutputContents());
                problem.setProblemClass(ProblemClass.valueOf(dto.getProblemClass()));
                problem.setProblemTimeLimit(dto.getProblemTimeLimit());
                problem.setProblemMemoryLimit(dto.getProblemMemoryLimit());
                problem.setProblemStatus(ProblemStatus.PRIVATE);
                problem.setProblemPrompt(dto.getProblemPrompt());

                // 기존의 테스트 케이스를 삭제하고 새로 추가
                testcaseRepository.deleteAll(problem.getTestcases());
                problem.getTestcases().clear();

                for (TestcaseRequestDto testcaseDto : dto.getTestcases()) {
                    Testcase testcase = new Testcase();
                    testcase.setTestcaseInput(testcaseDto.getTestcaseInput());
                    testcase.setTestcaseOutput(testcaseDto.getTestcaseOutput());
                    testcase.setTestcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()));
                    testcase.setProblem(problem);
                    problem.getTestcases().add(testcase);
                }

                problemRepository.save(problem);

                Question question = new Question();
                question.setExam(exam);
                question.setProblem(problem);
                question.setQuestionScore(dto.getQuestionScore() != null ? dto.getQuestionScore() : 10);
                question.setQuestionSequence(dto.getQuestionSequence());
                questionRepository.save(question);
            }
        }

        // 문제 삭제
        List<Long> currentProblemIds = problemRequestDtos.stream()
                .map(ProblemRequestDto::getProblemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Problem> existingProblems = existingQuestions.stream()
                .map(Question::getProblem)
                .collect(Collectors.toList());

        for (Problem existingProblem : existingProblems) {
            if (!currentProblemIds.contains(existingProblem.getProblemId())) {
                questionRepository.deleteByProblem(existingProblem);
                problemRepository.delete(existingProblem);
            }
        }
    }
    private void updateCandidateTests(Exam exam, List<Long> candidateIds) {
        // Exam 엔티티를 관리 상태로 가져옵니다.
        Exam managedExam = examRepository.findById(exam.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + exam.getExamId()));

        List<Test> existingTests = testRepository.findByExamExamId(managedExam.getExamId());
        Map<Long, Test> existingTestsMap = existingTests.stream()
                .collect(Collectors.toMap(Test::getMemberId, test -> test));

        // 중복된 candidateId 제거
        Set<Long> uniqueCandidateIds = new HashSet<>(candidateIds);

        uniqueCandidateIds.forEach(candidateId -> {
            Test test = existingTestsMap.getOrDefault(candidateId, new Test());
            test.setExam(managedExam);
            test.setMemberId(candidateId);

            if (test.getTestId() == null) {
                // New entity, persist it
                testRepository.save(test);
            } else {
                // Existing entity, merge it
                testRepository.save(test);
            }
        });

        existingTests.forEach(test -> {
            if (!uniqueCandidateIds.contains(test.getMemberId())) {
                testRepository.delete(test);
            }
        });
    }
    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);

    // 시험 문제 조회
    @Transactional(readOnly = true)
    public ExamDetailResponseDto getExamDetail(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");

        // 문제 목록 가져오기
        List<Problem> problems = questionRepository.findProblemsByExamId(examId);

        // ProblemDetailAdminResponseDto로 변환
        List<ProblemDetailAdminResponseDto> problemDetails = problems.stream()
                .map(ProblemDetailAdminResponseDto::new)
                .collect(Collectors.toList());

        // 후보자 목록 가져오기
        List<Long> candidateList = testRepository.findByExamExamId(examId).stream()
                .map(Test::getMemberId)
                .collect(Collectors.toList());

        logger.info("Candidate List: " + candidateList);


        // ExamDetailResponseDto 생성 및 반환
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
                exam.getExamDueTime(),
                candidateList, // CandidateList 추가
                problemDetails

        );
    }
    // 시험 삭제
    @Transactional
    public void deleteExam(Long examId, Long requestingMemberId) {
        // examId로 problemId 가져오기
        List<Long> problemIds = questionRepository.findProblemIdsByExamExamId(examId);
        // 시험 삭제 시 해당 시험에 대한 제출 삭제
        submissionServiceFeignClient.deleteSubmissionByProblemIds(problemIds);

        int affectedRows = examRepository.deleteByIdAndMemberId(examId, requestingMemberId);
        if (affectedRows == 0) {
            throw new RuntimeException("No exam found or unauthorized to delete this exam");
        }

    }

    // 시험 삭제 admin
    @Transactional
    public void deleteExamByAdmin(Long examId) {
        // examId로 problemId 가져오기
        List<Long> problemIds = questionRepository.findProblemIdsByExamExamId(examId);
        // 시험 삭제 시 해당 시험에 대한 제출 삭제
        submissionServiceFeignClient.deleteSubmissionByProblemIds(problemIds);
        examRepository.deleteById(examId);
    }
    // 시험 목록 조회 & 대회 목록 조회
    @Transactional(readOnly = true)
    public List<ExamCardInfoResponseDto> getExamList(Long memberId, String type, String status) {
        ExamType examType = ExamType.fromLabel(type);
        ExamStatus examStatus = ExamStatus.fromLabel(status);
        return getContests(memberId, examType, examStatus);
    }

    // 학생 시험 목록 조회
    @Transactional
    public List<TestOverviewResponseDto> getMemberTestList(Long memberId, String type, String status) {
        ExamType examType = ExamType.fromLabel(type);
        ExamStatus examStatus = ExamStatus.fromLabel(status);
        return getMemberTests(memberId, examType, examStatus);
    }

    private List<TestOverviewResponseDto> getMemberTests(Long memberId, ExamType examType, ExamStatus status) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH시");
        return testRepository.findByMemberId(memberId).stream()
                .filter(test -> test.getExam().getExamStatus() == status && test.getExam().getExamType() == examType)
                .map(test -> {
                    boolean isCompleted = test.getTestEndDate() != null;
                    String formattedStartDate = (test.getExam().getExamStartDate() != null) ? formatter.format(test.getExam().getExamStartDate()) : "";
                    String formattedEndDate = (test.getExam().getExamEndDate() != null) ? formatter.format(test.getExam().getExamEndDate()) : "";
                    return new TestOverviewResponseDto(
                            test.getTestId(),
                            test.getExam().getExamId(),
                            test.getExam().getExamTitle(),
                            formattedStartDate,
                            formattedEndDate,
                            isCompleted
                    );
                }).collect(Collectors.toList());
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

                    return new ExamCardInfoResponseDto(
                            exam.getExamId(),
                            exam.getExamTitle(),
                            memberNickname,
                            dateFormatter(exam.getExamStartDate()),
                            dateFormatter(exam.getExamEndDate()),
                            exam.getExamStatus().getLabel()
                    );
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

    // 시험 결과 목록 조회
    @Transactional(readOnly = true)
    public ExamResultPageDto getExamResultList(Long examId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "testEndDate"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + examId));

        Page<Test> tests = testRepository.findByExamExamId(examId, pageable);
        // 만약 응시 종료 시각이 없으면 응시하지 않은 시험으로 간주
        // TODO: 페이지네이션으로 추후 변경
        List<Test> filteredTests = tests.stream()
                .filter(test -> test.getTestEndDate() != null)
                .collect(Collectors.toList());

        List<ExamResultListDto> resultList = filteredTests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ExamResultPageDto(
                exam.getExamTitle(),
                exam.getExamMemo(),
                (int) tests.getTotalElements(),
                resultList
        );
    }

    // Test 엔티티를 ExamResultListDto로 변환
    private ExamResultListDto convertToDto(Test test) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        String testDueTime = "";
        String submissionDate = "";

        // testStartDate와 testEndDate가 모두 non-null인 경우에만 계산
        if (test.getTestStartDate() != null && test.getTestEndDate() != null) {
            testDueTime = Duration.between(test.getTestStartDate(), test.getTestEndDate()).toHoursPart() + ":"
                    + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toMinutesPart() + ":"
                    + Duration.between(test.getTestStartDate(), test.getTestEndDate()).toSecondsPart();
            submissionDate = test.getTestEndDate().format(dateTimeFormatter);
        } else {
            // 날짜 정보가 없는 경우 기본 문자열 설정
            testDueTime = "응시하지 않음";
            submissionDate = "응시하지 읺음";
        }

        ProblemMemberInfoResponseDto memberInfo = memberServiceFeignClient.getMemberInfo(test.getMemberId());

        return new ExamResultListDto(
                test.getTestId(),
                test.getMemberId(),
                memberInfo.getMemberName(),
                memberInfo.getMemberNumber(),
                memberInfo.getMemberEmail(),
                test.getTestScore(),
                testDueTime,
                submissionDate
        );
    }

    // 시험 결과 상세 조회
    @Transactional(readOnly = true)
    public ExamResultDetailsResponseDto getExamResults(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with id: " + testId));

        Exam exam = test.getExam();
        ProblemMemberInfoResponseDto memberInfo = memberServiceFeignClient.getMemberInfo(test.getMemberId());

        List<Question> questionsList = questionRepository.findByExamExamId(exam.getExamId());
        List<Long> problemId = questionsList.stream()
                .map(question -> question.getProblem().getProblemId())
                .collect(Collectors.toList());

        SubmissionExamResultInfoResponseDto submissionsInfo = submissionServiceFeignClient.fetchSubmissionsInfo(problemId, test.getMemberId());

        if (submissionsInfo == null || submissionsInfo.getSubmissions() == null) {
            throw new IllegalStateException("Submissions information is missing for the given test.");
        }

        Map<Long, Question> questionMap = questionsList.stream()
                .collect(Collectors.toMap(question -> question.getProblem().getProblemId(), Function.identity()));

        final int[] totalScore = {0};
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
                (int) testRepository.countByExamExamId(exam.getExamId()),
                memberInfo.getMemberName(),
                memberInfo.getMemberNumber(),
                memberInfo.getMemberEmail(),
                test.getTestScore(),
                testDueTime,
                submissionDate,
                questionDtos
        );
    }

    // 시험 점수 조회
    public Integer getTestScore(Long testId) {
        return testRepository.findById(testId)
                .map(Test::getTestScore)
                .orElseThrow(() -> new IllegalStateException("해당 테스트를 조회할 수 없습니다.: " + testId));
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
