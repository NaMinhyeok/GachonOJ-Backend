package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;

@Getter
public enum ProblemStatus {
    SAVED("저장"),
    REGISTERED("등록");
    private final String label;
    ProblemStatus(String label) {
        this.label = label;
    }
    public static ProblemStatus fromLabel(String status) {
        for (ProblemStatus problemStatus : ProblemStatus.values()) {
            if (problemStatus.getLabel().equals(status)) {
                return problemStatus;
            }
        }
        return null;
    }

}
