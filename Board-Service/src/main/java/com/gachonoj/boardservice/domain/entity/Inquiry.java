package com.gachonoj.boardservice.domain.entity;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private String inquiryTitle;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String inquiryContents;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime inquiryCreatedDate;
    @Enumerated(EnumType.STRING)
    private InquiryStatus inquiryStatus;
    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL)
    private Reply reply;
}
