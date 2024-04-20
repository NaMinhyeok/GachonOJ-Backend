package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.inquiryCreatedDate = inquiry.getInquiryUpdatedDate().format(formatter);
        this.inquiryStatus = (replyUpdateDate != null ? replyUpdateDate : "") + translateInquiryStatus(inquiry.getInquiryStatus()) ;
    }
    // ENUM To String
    public String translateInquiryStatus(InquiryStatus inquiryStatus) {
        return switch (inquiryStatus) {
            case NONE -> "대기중";
            case COMPLETED -> "답변완료";
        };
    }
}
