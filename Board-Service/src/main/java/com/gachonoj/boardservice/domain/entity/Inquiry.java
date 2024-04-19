package com.gachonoj.boardservice.domain.entity;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;
    private Long memberId;
    private String inquiryTitle;
    @Column(columnDefinition = "TEXT")
    private String inquiryContents;
    @Enumerated(EnumType.STRING)
    private InquiryStatus inquiryStatus = InquiryStatus.NONE;
    @CreatedDate
    private LocalDateTime inquiryCreatedDate;
    @LastModifiedDate
    private LocalDateTime inquiryUpdatedDate;

    public Inquiry(String inquiryTitle, String inquiryContents, Long memberId) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContents = inquiryContents;
        this.memberId = memberId;
    }

}

