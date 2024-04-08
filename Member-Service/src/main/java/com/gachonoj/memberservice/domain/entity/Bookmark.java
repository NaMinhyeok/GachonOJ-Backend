package com.gachonoj.memberservice.domain.entity;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private Long problemId;
}