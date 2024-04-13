package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.domain.entity.Notice;
import com.gachonoj.boardservice.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    // 공지사항 작성
    @Transactional
    public Notice createNotice(NoticeRequestDto requestDto) {
        Notice notice = Notice.create(
                requestDto.getNoticeTitle(),
                requestDto.getNoticeContents()
        );
        return noticeRepository.save(notice);
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}