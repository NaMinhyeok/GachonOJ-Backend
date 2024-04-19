package com.gachonoj.submissionservice.repository;

import com.gachonoj.submissionservice.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
