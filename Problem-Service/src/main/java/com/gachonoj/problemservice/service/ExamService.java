package com.gachonoj.problemservice.service;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import com.gachonoj.problemservice.domain.dto.request.CandidateListRequestDto;
import com.gachonoj.problemservice.domain.dto.request.ExamRequestDto;
import com.gachonoj.problemservice.domain.dto.request.TestcaseRequestDto;
import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.ScheduledContestResponseDto;
import com.gachonoj.problemservice.domain.entity.*;
import com.gachonoj.problemservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.problemservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ProblemRepository problemRepository;
    private final TestcaseRepository testcaseRepository;
    private final TestRepository testRepository;
    private final MemberServiceFeignClient memberServiceFeignClient;

    @Transactional
    public void registerExam(ExamRequestDto request, Long memberId) {
        Exam exam = new Exam();  // 실제 엔티티 클래스
        exam.setExamTitle(request.getExamTitle());
        exam.setMemberId(memberId);
        exam.setExamMemo(request.getExamMemo());
        exam.setExamNotice(request.getExamNotice());
        exam.setExamStartDate(LocalDateTime.parse(request.getExamStartDate()));
        exam.setExamEndDate(LocalDateTime.parse(request.getExamEndDate()));
        exam.setExamDueTime(request.getExamDueTime());
        exam.setExamStatus(request.getExamStatus());
        exam.setExamType(request.getExamType());

        examRepository.save(exam);  // 시험 정보 저장

        int questionSequence = 1;

        for (ProblemRequestDto problemRequestDto : request.getTests()) {
            Problem problem = new Problem();  // 실제 엔티티 클래스
            problem.setProblemTitle(problemRequestDto.getProblemTitle());
            problem.setProblemContents(problemRequestDto.getProblemContents());
            problem.setProblemClass(ProblemClass.valueOf(problemRequestDto.getProblemClass()));
            problem.setProblemTimeLimit(problemRequestDto.getProblemTimeLimit());
            problem.setProblemMemoryLimit(problemRequestDto.getProblemMemoryLimit());
            problem.setProblemStatus(ProblemStatus.valueOf(problemRequestDto.getProblemStatus()));
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
            question.setQuestionSequence(questionSequence++);
            question.setQuestionScore(10); // 점수는 예시값입니다, 실제 값으로 대체 필요
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

    @Transactional
    public void updateExam(Long examId, Long memberId, ExamRequestDto request) {
        // 시험 정보를 ID를 통해 찾아 업데이트
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));

        // 시험 정보 업데이트
        exam.setExamTitle(request.getExamTitle());
        exam.setMemberId(memberId);
        exam.setExamMemo(request.getExamMemo());
        exam.setExamNotice(request.getExamNotice());
        exam.setExamStartDate(LocalDateTime.parse(request.getExamStartDate()));
        exam.setExamEndDate(LocalDateTime.parse(request.getExamEndDate()));
        exam.setExamDueTime(request.getExamDueTime());
        exam.setExamStatus(request.getExamStatus());
        exam.setExamType(request.getExamType());
        exam.setExamUpdateDate(LocalDateTime.now());  // 현재 시간으로 업데이트 날짜 설정
        examRepository.save(exam);

        // 요청된 문제들만 업데이트
        for (ProblemRequestDto problemDto : request.getTests()) {
            if (problemDto.getProblemId() != null) {
                updateProblem(problemDto.getProblemId(), problemDto);
            }
        }
    }

    @Transactional
    public void deleteExam(Long examId, Long requestingMemberId) {
        int affectedRows = examRepository.deleteByIdAndMemberId(examId, requestingMemberId);
        if (affectedRows == 0) {
            throw new RuntimeException("No exam found or unauthorized to delete this exam");
        }
    }
    private void updateProblem(Long problemId, ProblemRequestDto problemDto) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));

        // 문제 정보 업데이트
        problem.setProblemTitle(problemDto.getProblemTitle());
        problem.setProblemContents(problemDto.getProblemContents());
        problem.setProblemClass(ProblemClass.valueOf(problemDto.getProblemClass()));
        problem.setProblemTimeLimit(problemDto.getProblemTimeLimit());
        problem.setProblemMemoryLimit(problemDto.getProblemMemoryLimit());
        problem.setProblemStatus(ProblemStatus.valueOf(problemDto.getProblemStatus()));
        problem.setProblemPrompt(problemDto.getProblemPrompt());
        problem.setProblemUpdatedDate(LocalDateTime.now());

        // 기존 테스트케이스를 삭제하고 새로 추가
        problem.getTestcases().clear();
        for (TestcaseRequestDto testcaseDto : problemDto.getTestcases()) {
            Testcase testcase = new Testcase();
            testcase.setTestcaseInput(testcaseDto.getTestcaseInput());
            testcase.setTestcaseOutput(testcaseDto.getTestcaseOutput());
            testcase.setTestcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()));
            testcase.setProblem(problem);
            problem.getTestcases().add(testcase);
        }

        problemRepository.save(problem);
    }

    public List<ScheduledContestResponseDto> getScheduledContests(Long memberId) {
        List<Exam> exams = examRepository.findScheduledContestsByMemberId(memberId);

        // DTO 변환
        return exams.stream()
                .map(exam -> {
                    String memberNickname = memberServiceFeignClient.getNicknames(exam.getMemberId());
                    return new ScheduledContestResponseDto(exam, memberNickname);
                })
                .collect(Collectors.toList());
    }

}
    /*@Transactional
    public void updateExam(Long examId, ExamRequestDto request, Long memberId) {
        // 기존 시험 정보 찾기
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));
        // 문제 ID 목록 가져오기
        List<Long> problemIds = questionRepository.getProblemIdByExamId(examId);

        // 기존 문제 업데이트
        for (Long problemId : problemIds) {
            // 문제 업데이트를 위해 문제 ID로 기존 문제 찾기
            Optional<Problem> optionalProblem = problemRepository.findById(problemId);
            if (optionalProblem.isPresent()) {
                Problem problem = optionalProblem.get();
                // 요청으로 전달된 문제 정보와 비교하여 업데이트 수행
                for (ProblemRequestDto problemDto : request.getTests()) {
                    if (problemDto.getProblemId() != null && problemDto.getProblemId().equals(problemId)) {
                        problem.update(
                                problemDto.getProblemTitle(),
                                problemDto.getProblemContents(),
                                problemDto.getProblemInputContents(),
                                problemDto.getProblemOutputContents(),
                                problemDto.getProblemDiff(),
                                ProblemClass.valueOf(problemDto.getProblemClass()),
                                problemDto.getProblemTimeLimit(),
                                problemDto.getProblemMemoryLimit(),
                                ProblemStatus.valueOf(problemDto.getProblemStatus()),
                                problemDto.getProblemPrompt()
                        ); // 문제 정보 업데이트
                        break; // 현재 문제 처리 완료했으므로 다음 문제로 이동
                    }
                }
            }
        }

        // 새로운 문제 추가
        for (ProblemRequestDto problemDto : request.getTests()) {
            if (problemDto.getProblemId() == null) {
                Problem newProblem = Problem.create(
                        problemDto.getProblemTitle(),
                        problemDto.getProblemContents(),
                        problemDto.getProblemInputContents(),
                        problemDto.getProblemOutputContents(),
                        problemDto.getProblemDiff(),
                        problemDto.getProblemTimeLimit(),
                        problemDto.getProblemMemoryLimit(),
                        problemDto.getProblemPrompt(),
                        ProblemClass.valueOf(problemDto.getProblemClass()),
                        ProblemStatus.valueOf(problemDto.getProblemStatus())
                ); // 새로운 문제 생성
                newProblem.setExam(exam);  // 문제를 시험에 연결

                // 새로운 문제의 테스트케이스 추가
                for (TestcaseRequestDto testcaseDto : problemDto.getTestcases()) {
                    Testcase testcase = createTestcaseFromDto(testcaseDto); // 테스트케이스 생성
                    testcase.setProblem(newProblem); // 새로운 문제에 연결
                    newProblem.addTestcase(testcase);
                }

                problemRepository.save(newProblem); // 새로운 문제 저장
            }
        }
    }

    // 시험 정보를 DTO에서 엔티티로 업데이트하는 메서드
    private void updateExamFromDto(Exam exam, ExamRequestDto request, Long memberId) {
        exam.setExamTitle(request.getExamTitle());
        exam.setMemberId(memberId);
        exam.setExamMemo(request.getExamMemo());
        exam.setExamNotice(request.getExamNotice());
        exam.setExamStartDate(request.getExamStartDate());
        exam.setExamEndDate(request.getExamEndDate());
        exam.setExamDueTime(request.getExamDueTime());
        exam.setExamStatus(request.getExamStatus());
        exam.setExamType(request.getExamType());
        examRepository.save(exam);
    }

    // DTO에서 엔티티로 테스트케이스 생성하는 메서드
    private Testcase createTestcaseFromDto(TestcaseRequestDto testcaseDto) {
        Testcase testcase = new Testcase();
        testcase.setTestcaseInput(testcaseDto.getTestcaseInput());
        testcase.setTestcaseOutput(testcaseDto.getTestcaseOutput());
        testcase.setTestcaseStatus(TestcaseStatus.valueOf(testcaseDto.getTestcaseStatus()));
        return testcase;
    }*/