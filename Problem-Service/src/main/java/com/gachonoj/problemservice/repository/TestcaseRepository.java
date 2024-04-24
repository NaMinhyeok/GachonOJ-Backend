package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<Testcase,Long> {
    // 문제 ID로 테스트케이스 조회
    List<Testcase> findByProblemProblemId(Long problemId);
}
