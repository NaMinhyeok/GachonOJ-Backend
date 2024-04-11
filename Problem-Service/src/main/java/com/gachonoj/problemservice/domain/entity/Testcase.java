package com.gachonoj.problemservice.domain.entity;

import com.gachonoj.problemservice.domain.constant.TestcaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testcaseInput;
    private String testcaseOutput;

    @Enumerated(EnumType.STRING)
    private TestcaseStatus testcaseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id") // 외래 키를 'problem_id'로 명시적으로 지정
    private Problem problem;
}