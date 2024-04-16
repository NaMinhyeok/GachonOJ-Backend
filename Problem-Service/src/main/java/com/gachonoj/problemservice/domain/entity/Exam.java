package com.gachonoj.problemservice.domain.entity;

import com.gachonoj.problemservice.domain.constant.ExamStatus;
import com.gachonoj.problemservice.domain.constant.ExamType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;
    @Column(nullable = false)
    private Long userId;
    private String examTitle;
    @Column(columnDefinition = "TEXT")
    private String examContents;
    @CreatedDate
    private LocalDateTime examCreatedDate;
    private LocalDateTime examUpdateDate;
    private LocalDateTime examStartDate;
    private LocalDateTime examEndDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExamStatus examStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExamType examType;
    private String examMemo;
    private String examNotice;
    private Integer examDueTime;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests;
}