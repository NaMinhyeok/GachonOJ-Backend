package com.gachonoj.memberservice.domain.dto.response;

import com.gachonoj.memberservice.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRankingResponseDto {
    private Long memberId;
    private Integer rating;
    private String memberNickname;
    private Integer memberRank;
    private Integer memberSolved;

    public MemberRankingResponseDto(Member member, Integer memberSolved) {
        this.memberId = member.getMemberId();
        this.memberNickname = member.getMemberNickname();
        this.memberRank = member.getMemberRank();
        this.memberSolved = memberSolved;
        this.rating = calculateRating(this.memberRank);
    }
    private Integer calculateRating(Integer memberRank) {
        if(memberRank < 1000) {
            return 0;
        } else if(memberRank < 1200) {
            return 1;
        } else if(memberRank < 1400) {
            return 2;
        } else if(memberRank < 1600) {
            return 3;
        } else if(memberRank < 1900) {
            return 4;
        } else if(memberRank < 2200) {
            return 5;
        } else if(memberRank < 2500) {
            return 6;
        } else {
            return 7;
        }
    }
}
