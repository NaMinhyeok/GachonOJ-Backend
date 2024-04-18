package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.memberId = :memberId")
    Integer getBookmarkCountByMemberId(Long memberId);

}