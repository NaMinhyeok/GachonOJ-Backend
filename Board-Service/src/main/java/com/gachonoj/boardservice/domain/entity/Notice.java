package com.gachonoj.boardservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
@Entity
@Table(name = "notice")
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
    @LastModifiedDate
    private LocalDateTime noticeUpdatedDate;

    // 공지사항 작성
    public Notice(String noticeTitle,String noticeContents,Long memberId){
        this.noticeTitle = noticeTitle;
        this.noticeContents = noticeContents;
        this.memberId = memberId;
    }
}

