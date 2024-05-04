package com.gachonoj.aiservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
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
}
