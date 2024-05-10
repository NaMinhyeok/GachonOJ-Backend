package com.gachonoj.submissionservice.feign.service;

import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.fegin.dto.response.SubmissionCodeInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.CorrectRateResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionResultCountResponseDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionFeignService {
    private final SubmissionRepository submissionRepository;

    // memberId로 제출 정보 조회
    public SubmissionMemberInfoResponseDto getMemberInfo(Long memberId) {
        int solvedProblem = submissionRepository.countSolvedProblemByMemberId(memberId);
        int tryProblem = submissionRepository.countTryProblemByMemberId(memberId);
        log.info("solvedProblem: {}, tryProblem: {}, memberId : {}", solvedProblem, tryProblem, memberId);
        return new SubmissionMemberInfoResponseDto(submissionRepository.countSolvedProblemByMemberId(memberId), submissionRepository.countTryProblemByMemberId(memberId));
    }

    // memberId로 푼 문제 수 조회
    public Integer getMemberSolved(Long memberId) {
        return submissionRepository.countSolvedProblemByMemberId(memberId);
    }


    // problemId로 정답자 수 조회
    public Integer getCorrectSubmission(Long problemId) {
        return submissionRepository.countCorrectSubmissionsByProblemId(problemId);
    }
    // 특정 문제의 정답률 계산
    public double getProblemCorrectRate(Long problemId) {
        Integer totalSubmissions = submissionRepository.countTotalSubmissionsByProblemId(problemId);
        Integer correctSubmissions = submissionRepository.countCorrectSubmissionsByProblemId(problemId);

        if (totalSubmissions == null || totalSubmissions == 0) {
            return 0.0; // 제출이 없으면 정답률은 0%
        }

        return (double) correctSubmissions / totalSubmissions * 100.0; // 정답률 계산
    }

    //memberId로 틀린 문제 리스트 받아옴
    public List<Long> getIncorrectProblemIdsByMemberId(Long memberId) {
        return submissionRepository.findIncorrectProblemIdsByMemberId(memberId);
    }
    //맞춘 문제 리스트 조회
    public List<Long> getCorrectProblemIdsByMemberId(Long memberId) {
        return submissionRepository.findCorrectProblemIdsByMemberId(memberId);
    }
    // problemId로 총 제출 수 조회
    public Integer getProblemSubmitCount(Long problemId) {
        return submissionRepository.countTotalSubmissionsByProblemId(problemId);
    }
    // 제출 번호 통해서 제출 코드, 문제 ID 가져오기
    public SubmissionCodeInfoResponseDto getSubmissionCodeInfo(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new IllegalArgumentException("해당 제출이 존재하지 않습니다."));
        return new SubmissionCodeInfoResponseDto(submission.getProblemId(), submission.getSubmissionCode());
    }
    // 오답률 높은 문제 TOP 5
    public List<CorrectRateResponseDto> getTop5IncorrectProblemList() {
        List<Long> top5IncorrectProblemIds = submissionRepository.findTop5IncorrectProblemIds();
        return top5IncorrectProblemIds.stream().map(problemId -> {
            double correctRate = getProblemCorrectRate(problemId);
            return new CorrectRateResponseDto(problemId, correctRate);
        }).toList();
    }
    // 오답률 높은 문제 분류 TOP 3를 가져오기 위한 문제 ID, 문제당 제출 개수, 오답 개수 조회
    public List<SubmissionResultCountResponseDto> getIncorrectProblemClass() {
        return submissionRepository.findIncorrectProblemClass();
    }
}
