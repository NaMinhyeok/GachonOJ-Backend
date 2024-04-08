package com.gachonoj.aiservice.repository;

import com.gachonoj.aiservice.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
}
