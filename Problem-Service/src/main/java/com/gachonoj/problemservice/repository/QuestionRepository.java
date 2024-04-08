package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {
}
