package com.gachonoj.memberservice.feign.service;

import com.gachonoj.memberservice.domain.entity.Member;
import com.gachonoj.memberservice.feign.dto.response.MemberNicknamesDto;
import com.gachonoj.memberservice.feign.dto.response.ProblemMemberInfoResponseDto;
import com.gachonoj.memberservice.feign.dto.response.SubmissionMemberRankInfoResponseDto;
import com.gachonoj.memberservice.repository.MemberRepository;
import com.gachonoj.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFeignService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    // memberId로 닉네임 조회
    public String getNicknameToBoard(Long memberId) {
        return memberRepository.findByMemberId(memberId).getMemberNickname();
    }
    // memberId로 member정보 조회
    public SubmissionMemberRankInfoResponseDto getMemberRank(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating  = memberService.calculateRating(member.getMemberRank());
        Integer needRank = memberService.calculateNeedRating(member.getMemberRank());
        Integer memberRank = member.getMemberRank();
        return new SubmissionMemberRankInfoResponseDto(memberRank,rating,needRank);
    }
    // memberId로 memberRank 갱신
    @Transactional
    public void updateMemberRank(Long memberId, Integer newRank) {
        Member member = memberRepository.findByMemberId(memberId);
        member.setMemberRank(newRank);
    }

    // memberId로 member 정보 조회
    public ProblemMemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        return new ProblemMemberInfoResponseDto(
                member.getMemberNumber(),
                member.getMemberName(),
                member.getMemberEmail()
        );
    }

    // 사용자 닉네임 조회 IN 절 이용
    public List<MemberNicknamesDto> getNicknames(List<Long> memberIds) {
        List<Member> members = memberRepository.findByMemberIdIn(memberIds);
        return members.stream()
                .map(member -> new MemberNicknamesDto(member.getMemberId(), member.getMemberNickname()))
                .collect(Collectors.toList());
    }
}
