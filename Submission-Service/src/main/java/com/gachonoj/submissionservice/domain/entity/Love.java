package com.gachonoj.submissionservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loveId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;
    private Long memberId;
}
