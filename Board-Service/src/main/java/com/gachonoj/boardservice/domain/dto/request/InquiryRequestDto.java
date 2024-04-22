package com.gachonoj.boardservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryRequestDto {
    @NotBlank
    private String inquiryTitle;
    @NotBlank
    private String inquiryContents;
}