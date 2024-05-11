package com.gachonoj.submissionservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultResponseDto {
    //0. 정답여부
    //1. 문제풀기전 경험치
    //2. 해당 문제 점수
    //3. 문제 푼 후 경험치
    //4. 등급 상승여부
    //5. 이전 등급
    //6. 이후 등급 인데
    //7. submissionId
    // 정답 여부
    private Boolean isCorrect;
    // 문제 풀기 전 경험치
    private Integer memberRank;
    // 해당 문제 점수
    private Integer problemRank;
    // 문제 푼 후 경험치
    private Integer afterMemberRank;
    // 등급 상승 여부
    private Boolean ratingChanged;
    // 이전 등급
    private Integer memberRating;
    // 이후 등급
    private Integer afterMemberRating;
    // submissionId
    private Long submissionId;
}
