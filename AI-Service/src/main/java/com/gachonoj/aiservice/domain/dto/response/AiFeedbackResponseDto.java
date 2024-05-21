package com.gachonoj.aiservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AiFeedbackResponseDto {
    private Long problemId;
    private String problemTitle;
    private String memberNickname;
    private String code;
    private String aiContents;
}
