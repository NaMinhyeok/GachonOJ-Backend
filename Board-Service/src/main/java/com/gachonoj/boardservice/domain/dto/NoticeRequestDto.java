package com.gachonoj.boardservice.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeRequestDto {
    private String noticeTitle;
    private String noticeContents;
}