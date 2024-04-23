package com.gachonoj.boardservice.domain.constant;

import lombok.Getter;

@Getter
public enum InquiryStatus {
    NONE("대기 중"),
    COMPLETED("답변 완료");
    private final String label;
    InquiryStatus(String label) {
        this.label = label;
    }

    public static InquiryStatus fromLabel(String status) {
        for (InquiryStatus inquiryStatus : InquiryStatus.values()) {
            if (inquiryStatus.getLabel().equals(status)) {
                return inquiryStatus;
            }
        }
        return null;
    }
}
