package com.gachonoj.submissionservice.domain.entity;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private Long problemId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String submissionCode;
    @CreatedDate
    private LocalDateTime submissionDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status submissionStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language submissionLang;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Love> loves;
}
