package com.gachonoj.problemservice.repository;

import com.gachonoj.problemservice.domain.entity.Bookmark;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    Page<Bookmark> findByMemberId(Long memberId, Pageable pageable);
    // 회원아이디로 북마크 문제 조회
    List<Bookmark> findByMemberId(Long memberId);
    Optional<Bookmark> findByMemberIdAndProblemProblemId(Long memberId, Long problemId);
    boolean existsByMemberIdAndProblemProblemId(Long memberId, Long problemId);
    // 문제 ID로 북마크인지 확인
    boolean existsBookmarkByProblemProblemId(Long problemId);
}
