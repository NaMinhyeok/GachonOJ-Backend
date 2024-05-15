package com.gachonoj.problemservice.feign.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemMemberInfoResponseDto {
    private String memberNumber;
    private String memberName;
    private String memberEmail;
}
