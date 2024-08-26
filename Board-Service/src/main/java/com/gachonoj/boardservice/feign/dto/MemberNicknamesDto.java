package com.gachonoj.boardservice.feign.dto;

public class MemberNicknamesDto {
    private Long memberId;
    private String memberNickname;

    public MemberNicknamesDto(Long memberId, String memberNickname) {
        this.memberId = memberId;
        this.memberNickname = memberNickname;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberNickname() {
        return memberNickname;
    }
}
