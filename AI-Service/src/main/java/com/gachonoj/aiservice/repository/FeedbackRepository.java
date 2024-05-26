package com.gachonoj.aiservice.repository;

import com.gachonoj.aiservice.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    // 오늘 사용된 ai 피드백 조회
    List<Feedback> findByFeedbackCreatedDateBetween(LocalDateTime start, LocalDateTime end); // 오늘 사용 토큰 조회
    // memberId로 ai 피드백 삭제
    void deleteByMemberId(Long memberId);
    // 문제에 대한 ai 분석 삭제
    void deleteByProblemId(Long problemId);
}
