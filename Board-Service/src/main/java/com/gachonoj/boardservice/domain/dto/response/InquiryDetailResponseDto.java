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

    public InquiryDetailResponseDto(Inquiry inquiry, Reply reply) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.replyContent = reply.getReplyContents();
    }

    public InquiryDetailResponseDto(Inquiry inquiry) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContents();
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.replyContent = null;
    }
    public String DateFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }
}
