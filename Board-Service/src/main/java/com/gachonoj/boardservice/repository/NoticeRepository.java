package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 메인 대시보드 공지사항 목록 조회 최대 5개
    List<Notice> findTop5ByOrderByNoticeCreatedDateDesc();
    // 공지사항 목록 조회 페이지네이션
    Page<Notice> findAllByOrderByNoticeCreatedDateDesc(Pageable pageable);
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    void deleteByMemberId(Long memberId);
}