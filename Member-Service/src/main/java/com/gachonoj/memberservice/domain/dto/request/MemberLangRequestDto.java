package com.gachonoj.memberservice.domain.dto.request;

import com.gachonoj.memberservice.domain.constant.MemberLang;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLangRequestDto {
    @NotBlank
    private MemberLang memberLang;
}
