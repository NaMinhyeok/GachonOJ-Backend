package com.gachonoj.problemservice.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestOverviewResponseDto {
    private Long testId;
    private Long examId;
    private String examTitle;
    private String examStartDate;
    private String examEndDate;
    private boolean completed;
}