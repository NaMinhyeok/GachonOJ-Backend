package com.gachonoj.memberservice.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommonResponseDto<T> {
    private boolean isSuccess;
    private int code;
    private LocalDateTime timestamp;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T reuslt;
    @Builder
    public CommonResponseDto(boolean isSuccess, int code, LocalDateTime timestamp, String message, T reuslt) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.timestamp = timestamp;
        this.message = message;
        this.reuslt = reuslt;
    }
    // 성공 응답
    public static <T> CommonResponseDto<T> success(T result) {
        return CommonResponseDto.<T>builder()
                .isSuccess(true)
                .code(200)
                .timestamp(LocalDateTime.now())
                .message("성공")
                .reuslt(result)
                .build();
    }
    //성공 응답 result 없이
    public static <T> CommonResponseDto<T> success() {
        return CommonResponseDto.<T>builder()
                .isSuccess(true)
                .code(200)
                .timestamp(LocalDateTime.now())
                .message("성공")
                .reuslt(null)
                .build();
    }
    // 실패 응답
    public static <T> CommonResponseDto<T> fail(int code, String message) {
        return CommonResponseDto.<T>builder()
                .isSuccess(false)
                .code(code)
                .timestamp(LocalDateTime.now())
                .message(message)
                .reuslt(null)
                .build();
    }
}
