package com.gachonoj.problemservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    @Column(nullable = false)
    private Long memberId;
    private Integer testScore;
    private LocalDateTime testStartDate;
    private LocalDateTime testEndDate;
}
