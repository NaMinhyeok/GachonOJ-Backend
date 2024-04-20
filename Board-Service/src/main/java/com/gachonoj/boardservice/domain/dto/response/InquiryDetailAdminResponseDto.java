package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.domain.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDetailAdminResponseDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String memberNickname;
    private String inquiryContent;
    private String inquiryCreatedDate;
    private String replyContent;

    public InquiryDetailAdminResponseDto(Inquiry inquiry, String memberNickname, Reply reply) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.replyContent = reply.getReplyContents();
    }

    public InquiryDetailAdminResponseDto(Inquiry inquiry, String memberNickname) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.replyContent = null;
    }

    // 날짜 포맷
    public String DateFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }
}
