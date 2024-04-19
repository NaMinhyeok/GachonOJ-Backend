package com.gachonoj.memberservice.feign.service;

import com.gachonoj.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFeignService {
    private final MemberRepository memberRepository;

    // memberId로 닉네임 조회
    public String getNicknameToBoard(Long memberId) {
        return memberRepository.findByMemberId(memberId).getMemberNickname();
    }
}
