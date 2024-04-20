package com.gachonoj.boardservice.domain.dto.response;

import com.gachonoj.boardservice.domain.constant.InquiryStatus;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryListResponseDto {
    private Long inquiryId;
    private String inquiryTitle;
    private String inquiryCreatedDate;
    private String inquiryStatus;

    public InquiryListResponseDto(Inquiry inquiry) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryCreatedDate = DateFormat(inquiry.getInquiryCreatedDate());
        this.inquiryStatus = translateInquiryStatus(inquiry.getInquiryStatus());
    }

    // ENUM To String
    public String translateInquiryStatus(InquiryStatus inquiryStatus) {
        return switch (inquiryStatus) {
            case NONE -> "대기중";
            case COMPLETED -> "답변완료";
        };
    }
    public String DateFormat(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }
}
