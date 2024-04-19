package com.gachonoj.problemservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    private Long memberId;

    public Bookmark(Long memberId, Problem problem){
        this.memberId = memberId;
        this.problem = problem;
    }
}