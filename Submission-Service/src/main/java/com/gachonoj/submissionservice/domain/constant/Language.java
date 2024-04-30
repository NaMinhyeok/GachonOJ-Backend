package com.gachonoj.submissionservice.domain.constant;

import lombok.Getter;

@Getter
public enum Language {
    C("C"),
    CPP("C++"),
    PYTHON("Python"),
    JAVA("Java"),
    JAVASCRIPT("JavaScript");

    private final String label;

    Language(String label) {
        this.label = label;
    }
    public static Language fromLabel(String type) {
        for (Language language : Language.values()) {
            if (language.getLabel().equals(type)) {
                return language;
            }
        }
        return null;
    }
}
