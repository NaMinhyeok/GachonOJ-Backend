package com.gachonoj.problemservice.repository;

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

    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = com.gachonoj.problemservice.domain.constant.ExamType.CONTEST AND e.examStartDate > CURRENT_TIMESTAMP")
    List<Exam> findScheduledContestsByMemberId(Long memberId);

    @Query("SELECT e FROM Exam e WHERE e.memberId = :memberId AND e.examType = com.gachonoj.problemservice.domain.constant.ExamType.CONTEST AND e.examStartDate < CURRENT_TIMESTAMP")
    List<Exam> findPastContestsByMemberId(Long memberId);
    // 회원 ID로 시험 목록 조회
    Page<Exam> findByMemberId(Long memberId, Pageable pageable);

    Page<Exam> findByExamType(ExamType examType, Pageable pageable);
}
