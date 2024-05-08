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
    private String inquiryTitle;
    private String memberNickname;
    private String inquiryContents;
    private String inquiryCreatedDate;
    private String replyContent;

    public InquiryDetailAdminResponseDto(Inquiry inquiry, String memberNickname,String inquiryCreatedDate, Reply reply) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryContents = inquiry.getInquiryContents();
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.replyContent = reply.getReplyContents();
    }

    public InquiryDetailAdminResponseDto(Inquiry inquiry, String memberNickname,String inquiryCreatedDate) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryContents = inquiry.getInquiryContents();
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.replyContent = null;
    }

}
