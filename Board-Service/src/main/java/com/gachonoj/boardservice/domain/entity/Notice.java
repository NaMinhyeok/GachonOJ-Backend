package com.gachonoj.boardservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;
    private Long memberId;
    private String noticeTitle;
    @Column(columnDefinition = "TEXT")
    private String noticeContents;
    @CreatedDate
    private LocalDateTime noticeCreatedDate;
}
