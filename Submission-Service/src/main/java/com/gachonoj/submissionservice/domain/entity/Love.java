package com.gachonoj.submissionservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Love {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loveId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "submission_id")
    private Submission submission;
    private Long memberId;
}
