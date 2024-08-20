package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Long> {
    // 문의사항 목록 조회 관리자 페이지네이션
    @EntityGraph(attributePaths = {"reply"})
    Page<Inquiry> findAllByOrderByInquiryCreatedDateDesc(Pageable pageable);
    // 문의사항 목록 조회 사용자 페이지네이션
    @EntityGraph(attributePaths = {"reply"})
    Page<Inquiry> findByMemberIdOrderByInquiryCreatedDateDesc(Long memberId, Pageable pageable);
    // 관리자 대시보드 최근 답변되지않은 문의사항 목록 조회
    @EntityGraph(attributePaths = {"reply"})
    List<Inquiry> findTop5ByInquiryStatusOrderByInquiryCreatedDateDesc(InquiryStatus inquiryStatus);
    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    void deleteByMemberId(Long memberId);
}
