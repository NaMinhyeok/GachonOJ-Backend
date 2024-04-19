package com.gachonoj.boardservice.service;

import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.domain.entity.Notice;
import com.gachonoj.boardservice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final NoticeRepository noticeRepository;

    //공지사항 작성
    public void createNotice(NoticeRequestDto noticeRequestDto,Long memberId) {
        Notice notice = new Notice(noticeRequestDto.getNoticeTitle(), noticeRequestDto.getNoticeContents(),memberId);
        noticeRepository.save(notice);
    }
}
