package com.gachonoj.memberservice.domain.dto.request;

import com.gachonoj.memberservice.domain.constant.MemberLang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLangRequestDto {
    private MemberLang memberLang;
}
