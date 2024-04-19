package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    //공지사항 생성 API
    @PostMapping("/admin/notice")
    public ResponseEntity<CommonResponseDto<Void>> createNotice(HttpServletRequest request,@RequestBody NoticeRequestDto noticeRequestDto){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        boardService.createNotice(noticeRequestDto, memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 공지사항 수정 API
    @PutMapping("/admin/notice/{noticeId}")
    public ResponseEntity<CommonResponseDto<Void>> updateNotice(HttpServletRequest request,@PathVariable Long noticeId,@RequestBody NoticeRequestDto noticeRequestDto){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        boardService.updateNotice(noticeId,noticeRequestDto, memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 공지사항 삭제 API

}

