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

    void deleteByProblem(Problem problem);

    // 문제 ID로 Question 가져오기
    Question findByProblemProblemId(Long problemId);

    // 시험 ID로 문제 ID 가져오기
    List<Question> findProblemIdsByExamExamId(Long examId);
    // 시험ID로 question 삭제
    void deleteByExamExamId(Long examId);
}
