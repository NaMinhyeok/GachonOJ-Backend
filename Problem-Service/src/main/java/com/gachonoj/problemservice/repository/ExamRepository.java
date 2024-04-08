package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam,Long> {
}
