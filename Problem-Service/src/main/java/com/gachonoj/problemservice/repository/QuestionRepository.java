package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Question;
import com.gachonoj.problemservice.domain.entity.Test;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByExamExamId(Long examId);

    @Query("SELECT q.problem FROM Question q WHERE q.exam.examId = :examId")
    List<Problem> findProblemsByExamId(@Param("examId") Long examId);
}
