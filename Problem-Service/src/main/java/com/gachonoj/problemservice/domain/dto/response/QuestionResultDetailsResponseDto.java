package com.gachonoj.problemservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDetailsResponseDto {
    private int questionSequence;
    private int questionScore;
    private Long problemId;
    private String problemTitle;
    private String problemContents;
    private boolean submissionStatus;
    private String submissionCode;
}
