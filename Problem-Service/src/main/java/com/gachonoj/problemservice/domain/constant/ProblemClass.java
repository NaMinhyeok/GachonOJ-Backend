package com.gachonoj.problemservice.domain.constant;

import lombok.Getter;

@Getter
public enum ProblemClass {
    BINARY_SERACH("이분 탐색"),
    GRAPH("그래프");

    private final String label;

    ProblemClass(String label) {
        this.label = label;
    }

    public static ProblemClass fromLabel(String classType) {
        for (ProblemClass problemClass : ProblemClass.values()) {
            if (problemClass.getLabel().equals(classType)) {
                return problemClass;
            }
        }
        return null;
    }
}
