package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.domain.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetailResponseDto {
    private String inquiryTitle;
    private String inquiryContent;
    private String inquiryCreatedDate;
    private String replyContent;

    public InquiryDetailResponseDto(Inquiry inquiry,String inquiryCreatedDate, Reply reply) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.replyContent = reply.getReplyContents();
    }

    public InquiryDetailResponseDto(Inquiry inquiry,String inquiryCreatedDate) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.replyContent = null;
    }
}
