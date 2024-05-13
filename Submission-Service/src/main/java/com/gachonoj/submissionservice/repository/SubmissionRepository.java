package com.gachonoj.submissionservice.repository;

import com.gachonoj.submissionservice.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
    // 푼 문제수 조회
    @Query("SELECT COUNT(DISTINCT s.problemId) FROM Submission s WHERE s.memberId = :memberId AND s.submissionStatus = 'CORRECT'")
    Integer countSolvedProblemByMemberId(@Param("memberId") Long memberId);
    // 시도한 문제수 조회
    @Query("SELECT COUNT(DISTINCT s.problemId) FROM Submission s WHERE s.memberId = :memberId AND s.submissionStatus = 'INCORRECT'")
    Integer countTryProblemByMemberId(@Param("memberId") Long memberId);

    // 정답자 수 조회
    @Query("SELECT COUNT(DISTINCT s.memberId) FROM Submission s WHERE s.problemId = :problemId AND s.submissionStatus = 'CORRECT'")
    Integer countCorrectSubmissionsByProblemId(@Param("problemId") Long problemId);

    // 총 제출 수 조회
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.problemId = :problemId")
    Integer countTotalSubmissionsByProblemId(@Param("problemId") Long problemId);

    // 틀린 문제 조회
    @Query("SELECT DISTINCT s.problemId FROM Submission s WHERE s.memberId = :memberId AND s.submissionStatus = com.gachonoj.submissionservice.domain.constant.Status.INCORRECT")
    List<Long> findIncorrectProblemIdsByMemberId(@Param("memberId") Long memberId);

    // 맞춘 문제 조회
    @Query("SELECT DISTINCT s.problemId FROM Submission s WHERE s.memberId = :memberId AND s.submissionStatus = com.gachonoj.submissionservice.domain.constant.Status.CORRECT")
    List<Long> findCorrectProblemIdsByMemberId(@Param("memberId") Long memberId);

    // Member ID와 Problem IDs를 기반으로 제출 정보를 가져오는 메서드 추가
    List<Submission> findByMemberIdAndProblemIdIn(Long memberId, List<Long> problemIds);

}
