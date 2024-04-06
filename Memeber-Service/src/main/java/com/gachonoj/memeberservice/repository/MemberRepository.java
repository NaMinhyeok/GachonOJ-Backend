package com.gachonoj.memeberservice.repository;

import com.gachonoj.memeberservice.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
