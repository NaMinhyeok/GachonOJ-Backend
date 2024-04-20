package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Long> {
    // 문의사항 목록 조회 페이지네이션
    Page<Inquiry> findAllByOrderByInquiryCreatedDateDesc(Pageable pageable);
}
