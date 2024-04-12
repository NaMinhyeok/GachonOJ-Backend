package com.gachonoj.boardservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    // 공지사항 작성
    @Builder(builderMethodName = "create")
    public static Notice create(String noticeTitle, String noticeContents) {
        Notice notice = new Notice();
        notice.noticeTitle = noticeTitle;
        notice.noticeContents = noticeContents;
        return notice;
    }
}

