package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Bookmark;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    Page<Bookmark> findByMemberId(Long memberId, Pageable pageable);
    boolean existsByMemberIdAndProblemProblemId(Long memberId, Long problemId);
}
