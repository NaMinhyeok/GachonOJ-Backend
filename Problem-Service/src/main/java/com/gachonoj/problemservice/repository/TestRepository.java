package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test,Long> {
    List<Test> findByExamId(Long examId);
}
