package com.gachonoj.memberservice.repository;

import com.gachonoj.memberservice.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
