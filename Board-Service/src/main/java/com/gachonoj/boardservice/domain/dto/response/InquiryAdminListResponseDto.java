package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
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
public class InquiryAdminListResponseDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String memberNickname;
    private String inquiryCreatedDate;
    private String inquiryStatus;

    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname, String inquiryCreatedDate, String replyUpdateDate) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.inquiryStatus = replyUpdateDate + " " + inquiry.getInquiryStatus().getLabel();
    }

    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname, String inquiryCreatedDate) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.inquiryStatus = null;
    }
    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname, String inquiryCreatedDate, InquiryStatus inquiryStatus) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.inquiryStatus = inquiryStatus.getLabel();
    }
}

