package com.gachonoj.aiservice.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsageResponseDto {
    private Long todayTokenUsage;
    private Long totalTokenUsage;
}
