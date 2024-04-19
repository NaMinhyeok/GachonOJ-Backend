package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Problem;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.memberId = :memberId")
    Integer getBookmarkCountByMemberId(Long memberId);

    Optional<Problem> findByProblemId(Long problemId);
}
