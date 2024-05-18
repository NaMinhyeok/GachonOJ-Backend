package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.entity.Love;
import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.repository.LoveRepository;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoveService {

    @Autowired
    private LoveRepository loveRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    // 좋아요 토글 기능
    @Transactional
    public void toggleLove(Long submissionId, Long memberId) {
        if (loveRepository.existsBySubmissionSubmissionIdAndMemberId(submissionId, memberId)) {
            loveRepository.deleteBySubmissionSubmissionIdAndMemberId(submissionId, memberId);
        } else {
            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new IllegalArgumentException("Submission not found with id: " + submissionId));
            Love love = new Love();
            love.setSubmission(submission);
            love.setMemberId(memberId);
            loveRepository.save(love);
        }
    }
}