package com.gachonoj.boardservice.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;
    @OneToOne
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;
    @Column(columnDefinition = "TEXT")
    private String replyContents;
    @CreatedDate
    private LocalDateTime replyCreatedDate;
}
