package com.gachonoj.boardservice.domain.entity;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
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

    // Custom static factory method using Builder pattern
    @Builder(builderMethodName = "create")
    public static Inquiry create(Long memberId, String inquiryTitle, String inquiryContents) {
        Inquiry inquiry = new Inquiry();
        inquiry.memberId = memberId;
        inquiry.inquiryTitle = inquiryTitle;
        inquiry.inquiryContents = inquiryContents;
        return inquiry;
    }

}

