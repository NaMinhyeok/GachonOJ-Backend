package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum ExamType {
    CONTEST("대회"),
    EXAM("시험");

    private final String label;
    ExamType(String label) {
        this.label = label;
    }
    public static ExamType fromLabel(String type) {
        for (ExamType examType : ExamType.values()) {
            if (examType.getLabel().equals(type)) {
                return examType;
            }
        }
        return null;
    }

}
