package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.InquiryRequestDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Transactional
    public Inquiry createInquiry(InquiryRequestDto requestDto) {
        Inquiry inquiry = Inquiry.create(
                requestDto.getMemberId(),
                requestDto.getInquiryTitle(),
                requestDto.getInquiryContents()
        );
        return inquiryRepository.save(inquiry);
    }
}