package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;

@Getter
public enum ExamStatus {
    //작성 중
    WRITING("작성 중"),
    // 예약
    RESERVATION("예약"),
    // 진행 중
    ONGOING("진행 중"),
    // 종료
    TERMINATED("종료");

    private final String label;
    ExamStatus(String label) {
        this.label = label;
    }
    public static ExamStatus fromLabel(String status) {
        for (ExamStatus examStatus : ExamStatus.values()) {
            if (examStatus.getLabel().equals(status)) {
                return examStatus;
            }
        }
        return null;
    }
}
