package com.gachonoj.problemservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    private Long memberId;
}