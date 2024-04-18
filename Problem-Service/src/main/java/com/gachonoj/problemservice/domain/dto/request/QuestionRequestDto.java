package com.gachonoj.problemservice.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionRequestDto {
    // problemId 필드에 대한 setter 메서드
    // getters and setters
    @Setter
    @Getter
    private Long problemId; // 연결할 문제 ID
    private Integer questionScore; // 문제에 대한 배점
    private Integer questionSequence; // 문제 순서

}