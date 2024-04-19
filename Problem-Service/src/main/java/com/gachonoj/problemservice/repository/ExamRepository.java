package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Exam;
import com.gachonoj.problemservice.domain.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<Exam,Long> {
    @Modifying
    @Query("DELETE FROM Exam e WHERE e.id = :examId AND e.memberId = :memberId")
    int deleteByIdAndMemberId(Long examId, Long memberId);
}
