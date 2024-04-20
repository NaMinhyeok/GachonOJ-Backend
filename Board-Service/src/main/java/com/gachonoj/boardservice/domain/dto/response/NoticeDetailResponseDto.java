package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDetailResponseDto {
    private String noticeTitle;
    private String noticeContent;
    private String noticeCreatedDate;

    public NoticeDetailResponseDto(Notice notice) {
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContents();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.noticeCreatedDate = notice.getNoticeUpdatedDate().format(formatter);
    }
}


