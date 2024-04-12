package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.ReplyRequestDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.domain.entity.Reply;
import com.gachonoj.boardservice.repository.InquiryRepository;
import com.gachonoj.boardservice.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private InquiryRepository inquiryRepository;

    // 문의 사항 답변
    @Transactional
    public Reply createReply(Long inquiryId, ReplyRequestDto requestDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry ID not found: " + inquiryId));

        Reply reply = Reply.create(inquiry, requestDto.getReplyContents());
        return replyRepository.save(reply);
    }

}