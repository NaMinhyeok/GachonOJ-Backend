package com.gachonoj.memberservice.repository;

import com.gachonoj.memberservice.domain.constant.Role;
import com.gachonoj.memberservice.domain.dto.response.HoverResponseDto;
import com.gachonoj.memberservice.domain.dto.response.MemberLangCountResponseDto;
import com.gachonoj.memberservice.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 학번 중복 확인
    boolean existsByMemberNumber(String memberNumber);
    // 이메일 중복 확인
    boolean existsByMemberEmail(String memberEmail);
    // 닉네임 중복 확인
    boolean existsByMemberNickname(String memberNickname);
    // 이메일로 회원 정보 가져오기
    Member findByMemberEmail(String memberEmail);
    // 멤버 아이디로 회원정보 조회
    Member findByMemberId(Long memberId);
    // 멤버 권한으로 회원정보 조회
    Page<Member> findByMemberRole(Role role, Pageable pageable);
    // 멤버 닉네임으로 학생 회원정보 조회
    Page<Member> findByMemberNicknameContainingAndMemberRole(String search, Role role, Pageable pageable);
    // 학번 또는 이메일 검색으로 학생 회원정보 조회
    List<Member> findByMemberEmailContainingAndMemberRoleOrMemberNumberContainingAndMemberRole(String memberEmail, Role memberRole, String memberNumber, Role memberRole2);
    // 학생 선호 언어 현황(각 언어마다 갯수) 조회
    @Query("SELECT new com.gachonoj.memberservice.domain.dto.response.MemberLangCountResponseDto(m.memberLang, COUNT(m.memberLang)) FROM Member m WHERE m.memberRole = 'ROLE_STUDENT' GROUP BY m.memberLang")
    List<MemberLangCountResponseDto> findLangCountByRole();
}
