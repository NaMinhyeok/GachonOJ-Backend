package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.constant.ExamStatus;
import com.gachonoj.problemservice.domain.constant.ExamType;
import com.gachonoj.problemservice.domain.entity.Exam;
import com.gachonoj.problemservice.domain.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam,Long> {
    @Modifying
    @Query("DELETE FROM Exam e WHERE e.id = :examId AND e.memberId = :memberId")
    int deleteByIdAndMemberId(Long examId, Long memberId);

    // 회원 ID로 시험 목록 조회
    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = com.gachonoj.problemservice.domain.constant.ExamType.CONTEST AND e.examStartDate > CURRENT_TIMESTAMP")
    List<Exam> findScheduledContestsByMemberId(Long memberId);
    // 회원 ID로 예정된 시험 목록 조회
    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = 'EXAM' AND e.examStartDate > CURRENT_TIMESTAMP")
    List<Exam> findScheduledExamByMemberId(Long memberId);
    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = com.gachonoj.problemservice.domain.constant.ExamType.CONTEST AND e.examStartDate < CURRENT_TIMESTAMP")
    List<Exam> findPastContestsByMemberId(Long memberId);
    // 회원 ID로 지난 시험 목록 조회
    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = 'EXAM' AND e.examStartDate < CURRENT_TIMESTAMP")
    List<Exam> findPastExamByMemberId(Long memberId);
    // 회원 ID로 시험 목록 조회
    Page<Exam> findByMemberId(Long memberId, Pageable pageable);

    Page<Exam> findByExamType(ExamType examType, Pageable pageable);

    List<Exam> findByExamStatusAndMemberId(ExamStatus examStatus, Long memberId);
}
