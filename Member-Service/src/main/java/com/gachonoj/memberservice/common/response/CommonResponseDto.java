package com.gachonoj.memberservice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gachonoj.memberservice.common.codes.ErrorCode;
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
    private T result;
    @Builder
    public CommonResponseDto(boolean isSuccess, int code, LocalDateTime timestamp, String message, T result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.timestamp = timestamp;
        this.message = message;
        this.result = result;
    }
    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(this);
    }
    // 성공 응답
    public static <T> CommonResponseDto<T> success(T result) {
        return CommonResponseDto.<T>builder()
                .isSuccess(true)
                .code(200)
                .timestamp(LocalDateTime.now())
                .message("성공")
                .result(result)
                .build();
    }
    //성공 응답 result 없이
    public static <T> CommonResponseDto<T> success() {
        return CommonResponseDto.<T>builder()
                .isSuccess(true)
                .code(200)
                .timestamp(LocalDateTime.now())
                .message("성공")
                .result(null)
                .build();
    }
    // 실패 응답
    public static <T> CommonResponseDto<T> fail(ErrorCode code, String message) {
        return CommonResponseDto.<T>builder()
                .isSuccess(false)
                .code(code.getStatus())
                .timestamp(LocalDateTime.now())
                .message(message)
                .result(null)
                .build();
    }
}
