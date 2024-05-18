package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Exam;
import com.gachonoj.problemservice.domain.entity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test,Long> {
    List<Test> findByExamExamId(Long examId);
    // 시험아이디와 회원아이디로 응시테이블 가져오기
    Test findByExamExamIdAndMemberId(Long examId, Long memberId);
    Integer countByExamExamId(Long examId);
    Page<Test> findByExamExamId(Long examId, Pageable pageable);
    // MemberId로 응시한 시험 확인
    List<Test> findByMemberId(Long memberId);

}
