package com.gachonoj.submissionservice.domain.constant;

import lombok.Getter;

@Getter
public enum Status {
    CORRECT("정답"),
    INCORRECT("오답");

    private final String label;
    Status(String label) {
        this.label = label;
    }

}
