package com.gachonoj.boardservice.feign.service;

import com.gachonoj.boardservice.repository.InquiryRepository;
import com.gachonoj.boardservice.repository.NoticeRepository;
import com.gachonoj.boardservice.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardFeignService {
    private final NoticeRepository noticeRepository;
    private final InquiryRepository inquiryRepository;

    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @Transactional
    public void deleteBoardByMemberId(Long memberId) {
        noticeRepository.deleteByMemberId(memberId);
        inquiryRepository.deleteByMemberId(memberId);
    }
}
