package com.gachonoj.memberservice.domain.dto.response;

import com.gachonoj.memberservice.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberListResponseDto {
    private Long memberId;
    private String memberEmail;
    private String memberName;
    private String memberNickname;
    private String memberNumber;
    private String memberRole;
    private String memberCreatedDate;

    public MemberListResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.memberEmail = member.getMemberEmail();
        this.memberName = member.getMemberName();
        this.memberNickname = member.getMemberNickname();
        this.memberNumber = member.getMemberNumber();
        this.memberRole = String.valueOf(member.getMemberRole());
        this.memberCreatedDate = member.getMemberCreatedDate().toString();
    }
}
