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
    Page<Exam> findByMemberId(Long memberId, Pageable pageable);

    Page<Exam> findByExamType(ExamType examType, Pageable pageable);

    List<Exam> findByExamStatusAndMemberId(ExamStatus examStatus, Long memberId);

    // 작성중인 것 제외하고 exam 조회
    List<Exam> findByExamStatusNot(ExamStatus examStatus);

    // MemberId로 시험 삭제
    void deleteByMemberId(Long memberId);
}
