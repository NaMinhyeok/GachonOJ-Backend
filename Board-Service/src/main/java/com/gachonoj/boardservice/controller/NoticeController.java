package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.domain.dto.NoticeRequestDto;
import com.gachonoj.boardservice.domain.entity.Notice;
import com.gachonoj.boardservice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board/admin")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 생성 API
    @PostMapping("/notice")
    public ResponseEntity<CommonResponseDto<Notice>> createNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
        Notice notice = noticeService.createNotice(noticeRequestDto);
        // CommonResponseDto를 사용하여 성공 메시지와 함께 Notice 객체를 반환
        return ResponseEntity.ok(CommonResponseDto.success(notice));
    }
}