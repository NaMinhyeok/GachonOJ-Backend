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

    public InquiryListResponseDto(Inquiry inquiry,String inquiryCreatedDate) {
        this.inquiryId = inquiry.getInquiryId();
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryCreatedDate = inquiryCreatedDate;
        this.inquiryStatus = inquiry.getInquiryStatus().getLabel();
    }
}
