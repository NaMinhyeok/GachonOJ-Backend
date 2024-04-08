package com.gachonoj.problemservice.domain.entity;

import com.gachonoj.problemservice.domain.constant.ProblemClass;
import com.gachonoj.problemservice.domain.constant.ProblemStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;
    private String problemTitle;
    @Column(columnDefinition = "TEXT")
    private String problemContents;
    private Integer problemDiff;
    @Enumerated(EnumType.STRING)
    private ProblemClass problemClass;
    private Integer problemTimeLimit;
    private Integer problemMemoryLimit;
    @Column(columnDefinition = "TEXT")
    private String problemInputContents;
    @Column(columnDefinition = "TEXT")
    private String problemOutputContents;
    @Enumerated(EnumType.STRING)
    private ProblemStatus problemStatus;
    @CreatedDate
    private LocalDateTime problemCreatedDate;
    private LocalDateTime problemUpdatedDate;
    private String problemPrompt;
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Testcase> testcases;
}
