package com.gachonoj.submissionservice.utils.querycounter;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
public class ApiQueryCounter {
    private int queryCount = 0;

    public void increaseQueryCount() {
        queryCount++;
    }

    public boolean isWarn() {
        return queryCount >= 10;
    }
}
