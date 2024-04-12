package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}