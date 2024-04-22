package com.gachonoj.memberservice.domain.constant;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_STUDENT("학생"),
    ROLE_PROFESSOR("교수"),
    ROLE_ADMIN("관리자");
    private final String label;
    Role(String label) {
        this.label = label;
    }

    public static Role fromLabel(String label) {
        for (Role role : Role.values()) {
            if (role.getLabel().equals(label)) {
                return role;
            }
        }
        return null;
    }
}

