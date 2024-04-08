package com.gachonoj.problemservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id",nullable = false)
    private Exam exam;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id",nullable = false)
    private Problem problem;
    private Integer questionSequence;
    private Integer questionScore;
}
