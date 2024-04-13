package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, PagingAndSortingRepository<Notice, Long> {
    // Additional custom methods can be defined here
}