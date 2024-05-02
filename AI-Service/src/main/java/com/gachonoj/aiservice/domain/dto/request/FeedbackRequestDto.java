package com.gachonoj.aiservice.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDto {
    private String code;
    private String prompt;
}
