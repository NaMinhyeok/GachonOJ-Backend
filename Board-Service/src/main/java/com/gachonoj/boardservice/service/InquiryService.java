package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.InquiryRequestDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.repository.InquiryRepository;
import com.gachonoj.boardservice.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private ReplyRepository replyRepository;
    // 문의사항 작성
    @Transactional
    public Inquiry createInquiry(InquiryRequestDto requestDto) {
        Inquiry inquiry = Inquiry.create(
                requestDto.getMemberId(),
                requestDto.getInquiryTitle(),
                requestDto.getInquiryContents()
        );
        return inquiryRepository.save(inquiry);
    }

    // 문의사항 삭제
    @Transactional
    public void deleteInquiry(Long inquiryId) {
        replyRepository.deleteByInquiryId(inquiryId);  // 먼저 답변 삭제
        inquiryRepository.deleteById(inquiryId);  // 그 후 문의사항 삭제
    }
}