package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListResponseDto {
    private Long noticeId;
    private String noticeTitle;
    private String memberNickname;
    private String noticeCreatedDate;

    public NoticeListResponseDto(Notice notice, String createdDate, String memberNickname) {
        this.noticeId = notice.getNoticeId();
        this.noticeTitle = notice.getNoticeTitle();
        this.memberNickname = memberNickname;
        this.noticeCreatedDate = createdDate;
    }
}
