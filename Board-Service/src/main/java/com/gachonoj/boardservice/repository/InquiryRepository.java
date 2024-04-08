package com.gachonoj.boardservice.repository;

import com.gachonoj.boardservice.domain.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry,Long> {
}
