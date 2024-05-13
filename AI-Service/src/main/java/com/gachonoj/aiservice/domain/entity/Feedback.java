package com.gachonoj.aiservice.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;
    @Column(nullable = false)
    private Long submissionId;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private Long problemId;
    @Column(columnDefinition = "TEXT",nullable = false)
    private String aiContents;
    @Column(nullable = false)
    private Integer totalTokens;
    @CreatedDate
    private LocalDateTime feedbackCreatedDate;

    @Builder
    public Feedback(Long submissionId, Long memberId, Long problemId, String aiContents, Integer totalTokens) {
        this.submissionId = submissionId;
        this.memberId = memberId;
        this.problemId = problemId;
        this.aiContents = aiContents;
        this.totalTokens = totalTokens;
    }
}
