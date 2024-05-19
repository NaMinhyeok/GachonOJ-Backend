package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.domain.entity.Question;
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


    // 문제 검색
    Optional<Problem> findByProblemId(Long problemId);

    // 로그인한 회원 type에 따른 문제 목록 조회에 이용되는 메소드
    Page<Problem> findAllByProblemIdInAndProblemStatus(List<Long> ids, ProblemStatus problemStatus ,Pageable pageable);
    // 문제 제목으로 검색
    // 등록된 문제 제목으로 검색
    Page<Problem> findByProblemTitleContainingAndProblemStatus(String search, ProblemStatus problemStatus, Pageable pageable);
    // 등록된 문제 분류별 조회
    Page<Problem> findByProblemClassAndProblemStatus(ProblemClass problemClass, ProblemStatus problemStatus, Pageable pageable);
    // 등록된 문제 난이도별 조회
    Page<Problem> findByProblemDiffAndProblemStatus(int i, ProblemStatus problemStatus, Pageable pageable);
    // 등록된 문제 추천 문제 조회 6개
    List<Problem> findTop6ByProblemStatusOrderByProblemCreatedDateDesc(ProblemStatus problemStatus);
    // 등록된 문제 전체 조회
    Page<Problem> findByProblemStatus(ProblemStatus problemStatus, Pageable pageable);
    // 등록된 문제 전체 조회(비공개 문제 제외)
    Page<Problem> findByProblemStatusNot(ProblemStatus problemStatus, Pageable pageable);

    // 문제 제목으로 검색 (비공개 문제 제외)
    Page<Problem> findByProblemTitleContainingAndProblemStatusNot(String search, ProblemStatus problemStatus, Pageable pageable);
}
