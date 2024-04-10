package com.gachonoj.memberservice.repository;

import com.gachonoj.memberservice.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 학번 중복 확인
    boolean existsByMemberNumber(String memberNumber);
    // 이메일 중복 확인
    boolean existsByMemberEmail(String memberEmail);

    // 이메일로 회원 정보 가져오기
    Member findByMemberEmail(String memberEmail);
}
