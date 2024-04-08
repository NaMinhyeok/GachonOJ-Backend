package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
}
