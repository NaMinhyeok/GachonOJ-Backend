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
    private String memberNickname;
    private String noticeTitle;
    private String noticeContents;
    private String noticeCreatedDate;

    public NoticeDetailResponseDto(Notice notice,String createdDate,String memberNickname) {
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContents = notice.getNoticeContents();
        this.noticeCreatedDate = createdDate;
        this.memberNickname = memberNickname;
    }
}


