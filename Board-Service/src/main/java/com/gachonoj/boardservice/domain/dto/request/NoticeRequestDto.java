package com.gachonoj.boardservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeRequestDto {
    @NotBlank
    private String noticeTitle;
    @NotBlank
    private String noticeContents;
}