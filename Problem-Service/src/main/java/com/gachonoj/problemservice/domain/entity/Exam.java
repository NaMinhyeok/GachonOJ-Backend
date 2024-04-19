package com.gachonoj.problemservice.domain.entity;
import com.gachonoj.problemservice.domain.constant.ExamStatus;
import com.gachonoj.problemservice.domain.constant.ExamType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)  // examCreatedDate를 받기 위한 어노테이션
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;
    @Column(nullable = false)
    private Long memberId;
    private String examTitle;
    @Column(columnDefinition = "TEXT")
    private String examContents;
    @CreatedDate
    private LocalDateTime examCreatedDate;
    private LocalDateTime examUpdateDate;
    private String examStartDate;
    private String examEndDate;
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