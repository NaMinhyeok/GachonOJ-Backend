package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 메인 대시보드 공지사항 목록 조회 최대 5개
    List<Notice> findTop5ByOrderByNoticeCreatedDateDesc();

}