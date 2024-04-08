package com.gachonoj.problemservice.domain.entity;

import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testcaseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    private String testcaseInput;
    private String testcaseOutput;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestcaseStatus testcaseStatus;
}
