package com.gachonoj.memberservice.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class MemberInfoRequestDto {
    @NotBlank
    private String memberNickname;
    @NotBlank
    private String memberName;
    private String memberIntroduce;
}
