package com.gachonoj.problemservice.domain.entity;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
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
@EntityListeners(AuditingEntityListener.class)  // problemCreatedDate를 받기 위한 어노테이션
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;
    private String problemTitle;
    @Column(columnDefinition = "TEXT")
    private String problemContents;
    @Column(columnDefinition = "TEXT")
    private String problemInputContents;
    @Column(columnDefinition = "TEXT")
    private String problemOutputContents;
    private Integer problemDiff;
    @Enumerated(EnumType.STRING)
    private ProblemClass problemClass;
    private Integer problemTimeLimit;
    private Integer problemMemoryLimit;
    @Enumerated(EnumType.STRING)
    private ProblemStatus problemStatus;
    @CreatedDate
    private LocalDateTime problemCreatedDate;
    private LocalDateTime problemUpdatedDate;
    private String problemPrompt;
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Testcase> testcases = new ArrayList<>();
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks;

    public void addTestcase(Testcase testcase) {
        if (this.testcases == null) {
            this.testcases = new ArrayList<>();
        }
        this.testcases.add(testcase);
        testcase.setProblem(this);
    }

    // Problem 클래스 내부에 빌더 패턴을 커스텀하기
    @Builder
    public static Problem create(String problemTitle, String problemContents, String problemInputContents, String problemOutputContents,
                                 Integer problemDiff, Integer problemTimeLimit, Integer problemMemoryLimit,
                                 String problemPrompt, ProblemClass problemClass, ProblemStatus problemStatus) {
        Problem problem = new Problem();
        problem.problemTitle = problemTitle;
        problem.problemContents = problemContents;
        problem.problemInputContents = problemInputContents;
        problem.problemOutputContents = problemOutputContents;
        problem.problemDiff = problemDiff;
        problem.problemTimeLimit = problemTimeLimit;
        problem.problemMemoryLimit = problemMemoryLimit;
        problem.problemPrompt = problemPrompt;
        problem.problemClass = problemClass;
        problem.problemStatus = problemStatus;
        problem.testcases = new ArrayList<>(); // 초기화
        return problem;
    }

    // 문제 수정
    public void update(String problemTitle, String problemContents, String problemInputContents, String problemOutputContents,
                       Integer problemDiff, ProblemClass problemClass, Integer problemTimeLimit, Integer problemMemoryLimit,
                       ProblemStatus problemStatus, String problemPrompt) {
        this.problemTitle = problemTitle;
        this.problemContents = problemContents;
        this.problemInputContents = problemInputContents;
        this.problemOutputContents = problemOutputContents;
        this.problemDiff = problemDiff;
        this.problemClass = problemClass;
        this.problemTimeLimit = problemTimeLimit;
        this.problemMemoryLimit = problemMemoryLimit;
        this.problemStatus = problemStatus;
        this.problemPrompt = problemPrompt;
        this.problemUpdatedDate = LocalDateTime.now(); // 현재 시간으로 업데이트 날짜 설정
    }
}