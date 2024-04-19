package com.gachonoj.submissionservice.fegin.service;

import com.gachonoj.submissionservice.fegin.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionFeignService {
    private final SubmissionRepository submissionRepository;

    // memberId로 제출 정보 조회
    public SubmissionMemberInfoResponseDto getMemberInfo(Long memberId) {
        int solvedProblem = submissionRepository.countSolvedProblemByMemberId(memberId);
        int tryProblem = submissionRepository.countTryProblemByMemberId(memberId);
        log.info("solvedProblem: {}, tryProblem: {}, memberId : {}", solvedProblem, tryProblem,memberId);
        return new SubmissionMemberInfoResponseDto(submissionRepository.countSolvedProblemByMemberId(memberId), submissionRepository.countTryProblemByMemberId(memberId));
    }
    // memberId로 푼 문제 수 조회
    public Integer getMemberSolved(Long memberId) {
        return submissionRepository.countSolvedProblemByMemberId(memberId);
    }
}
