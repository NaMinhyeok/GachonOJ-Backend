package com.gachonoj.submissionservice.repository;

import com.gachonoj.submissionservice.domain.constant.Status;
import com.gachonoj.submissionservice.domain.dto.response.TodaySubmissionCountResponseDto;
import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionResultCountResponseDto;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    // 오답률 높은 문제 Top 5
    @Query("SELECT s.problemId FROM Submission s WHERE s.submissionStatus = 'INCORRECT' GROUP BY s.problemId ORDER BY COUNT(s.problemId) DESC LIMIT 5")
    List<Long> findTop5IncorrectProblemIds();
    // 금일 채점 결과 현황 조회
    @Query("SELECT COUNT(s), SUM(CASE WHEN s.submissionStatus = 'CORRECT' THEN 1 ELSE 0 END), SUM(CASE WHEN s.submissionStatus = 'INCORRECT' THEN 1 ELSE 0 END) FROM Submission s WHERE s.submissionDate = CURRENT_DATE")
    TodaySubmissionCountResponseDto countTodaySubmissions();

    // 금일 채점 결과 현황 조회
    List<Submission> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);

    // 오답률 높은 문제 분류 TOP 3를 가져오기 위한 문제 ID, 문제당 제출 개수, 오답 개수 조회
    @Query("SELECT s.problemId, COUNT(s.problemId), SUM(CASE WHEN s.submissionStatus = 'INCORRECT' THEN 1 ELSE 0 END) FROM Submission s GROUP BY s.problemId ORDER BY COUNT(s.problemId) DESC")
    List<SubmissionResultCountResponseDto> findIncorrectProblemClass();
}
