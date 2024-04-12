package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 답변과 같이 삭제될 수 있도록 설정
    @Transactional
    @Modifying
    @Query("DELETE FROM Reply r WHERE r.inquiry.inquiryId = :inquiryId")
    void deleteByInquiryId(Long inquiryId);
}