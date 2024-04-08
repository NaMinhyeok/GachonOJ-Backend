package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestcaseRepository extends JpaRepository<Testcase,Long> {
}
