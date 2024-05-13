package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test,Long> {
    List<Test> findByExamExamId(Long examId);
    Integer countByExamExamId(Long examId);
    Page<Test> findByExamExamId(Long examId, Pageable pageable);
}
