package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;

@Getter
public enum TestcaseStatus {
    VISIBLE("공개"),
    INVISIBLE("비공개");
    private final String label;
    TestcaseStatus(String label) {
        this.label = label;
    }
    public static TestcaseStatus fromLabel(String status) {
        for (TestcaseStatus testcaseStatus : TestcaseStatus.values()) {
            if (testcaseStatus.getLabel().equals(status)) {
                return testcaseStatus;
            }
        }
        return null;
    }
}
