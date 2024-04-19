package com.gachonoj.memberservice.domain.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class MemberInfoRequestDto {
    private String memberNickname;
    private String memberName;
    private String memberIntroduce;
}
