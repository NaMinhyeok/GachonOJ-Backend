package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.entity.Problem;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.memberId = :memberId")
    Integer getBookmarkCountByMemberId(Long memberId);

    Optional<Problem> findByProblemId(Long problemId);


    Page<Problem> findAllByProblemIdIn(List<Long> ids, Pageable pageable);
    // 문제 제목으로 검색
    Page<Problem> findByProblemTitleContaining(String search, Pageable pageable);
    // 문제 분류별 조회
    Page<Problem> findByProblemClass(ProblemClass problemClass, Pageable pageable);
    // 난이도별 문제 조회
    Page<Problem> findByProblemDiff(int i, Pageable pageable);
    // 추천 문제 조회 6개
    List<Problem> findTop6ByOrderByProblemCreatedDateDesc();
}
