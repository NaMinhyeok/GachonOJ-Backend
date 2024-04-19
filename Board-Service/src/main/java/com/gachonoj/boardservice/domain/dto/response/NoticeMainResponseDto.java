package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeMainResponseDto {
    private String noticeTitle;
    private String memberNickname;
    private String noticeCreatedDate;

    public NoticeMainResponseDto(Notice notice,String memberNickname) {
        this.noticeTitle = notice.getNoticeTitle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.noticeCreatedDate = notice.getNoticeUpdatedDate().format(formatter);
        this.memberNickname = memberNickname;
    }
}
