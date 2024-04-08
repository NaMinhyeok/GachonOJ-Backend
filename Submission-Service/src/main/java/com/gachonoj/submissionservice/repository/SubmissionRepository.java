package com.gachonoj.submissionservice.repository;

import com.gachonoj.submissionservice.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
}
