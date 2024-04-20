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

    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname, String replyUpdateDate) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.inquiryStatus = (replyUpdateDate != null ? replyUpdateDate : "") + translateInquiryStatus(inquiry.getInquiryStatus());
    }

    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.inquiryStatus = null;
    }

    public InquiryAdminListResponseDto(Inquiry inquiry, String memberNickname, Reply reply) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.memberNickname = memberNickname;
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.inquiryStatus =  DateFormat(reply.getReplyUpdatedDate()) + translateInquiryStatus(inquiry.getInquiryStatus());
    }

    // ENUM To String
    public String translateInquiryStatus(InquiryStatus inquiryStatus) {
        return switch (inquiryStatus) {
            case NONE -> "대기중";
            case COMPLETED -> "답변완료";
        };
    }
    // 날짜 포맷
    public String DateFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }
}
