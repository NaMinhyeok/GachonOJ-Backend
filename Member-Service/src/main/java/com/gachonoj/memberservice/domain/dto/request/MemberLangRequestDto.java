package com.gachonoj.memberservice.domain.dto.request;

import com.gachonoj.memberservice.domain.constant.MemberLang;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLangRequestDto {
    @NotNull
    private MemberLang memberLang;
}
