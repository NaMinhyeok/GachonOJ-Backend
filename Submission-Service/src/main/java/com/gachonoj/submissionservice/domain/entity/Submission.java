package com.gachonoj.submissionservice.domain.entity;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private Long problemId;
    @Column(nullable = false)
    private String submissionCode;
    @CreatedDate
    private LocalDateTime submissionDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status submissionStatus;
//    private Integer submissionTime;
//    private Integer submissionMemory;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language submissionLang;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Love> loves;

    @Builder
    public Submission(Long memberId, Long problemId, String submissionCode, Status submissionStatus, Language submissionLang) {
        this.memberId = memberId;
        this.problemId = problemId;
        this.submissionCode = submissionCode;
        this.submissionStatus = submissionStatus;
        this.submissionLang = submissionLang;
    }
}
