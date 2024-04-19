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
    // Member Role 한글 String으로 변환
    public String getMemberRole() {
        if (this.memberRole.equals("ROLE_STUDENT")) {
            return "학생";
        } else if (this.memberRole.equals("ROLE_PROFESSOR")) {
            return "교수";
        } else if (this.memberRole.equals("ROLE_ADMIN")) {
            return "관리자";
        } else {
            return "알 수 없음";
        }
    }
    // Date 형식 변경
    // LocalDateTime을 YYYY.MM.DD로 변경
    public String getMemberCreatedDate() {
        LocalDateTime dateTime = LocalDateTime.parse(this.memberCreatedDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dateTime.format(formatter);
    }
}
