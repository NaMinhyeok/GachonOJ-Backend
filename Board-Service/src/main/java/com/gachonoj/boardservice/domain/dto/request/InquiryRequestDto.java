package com.gachonoj.boardservice.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryRequestDto {
    private Long memberId;
    private String inquiryTitle;
    private String inquiryContents;
    private String inquiryStatus; // This should be a string representation of the InquiryStatus enum
}