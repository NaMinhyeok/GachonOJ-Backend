package com.gachonoj.boardservice.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryRequestDto {
    private String inquiryTitle;
    private String inquiryContents;
}