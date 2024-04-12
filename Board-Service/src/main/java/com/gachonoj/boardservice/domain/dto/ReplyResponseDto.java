package com.gachonoj.boardservice.domain.dto;

import com.gachonoj.boardservice.domain.entity.Reply;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ReplyResponseDto {
    private Long replyId;
    private String replyContents;
    private String replyCreatedDate;  // 날짜를 문자열로 변환하여 저장

    // replyCreatedDate를 받기 위한 ResponseDto
    public ReplyResponseDto(Reply reply) {
        this.replyId = reply.getReplyId();
        this.replyContents = reply.getReplyContents();
        this.replyCreatedDate = reply.getReplyCreatedDate() != null ?
                reply.getReplyCreatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}