package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.domain.dto.request.InquiryRequestDto;
import com.gachonoj.boardservice.domain.dto.request.NoticeRequestDto;
import com.gachonoj.boardservice.domain.dto.request.ReplyRequestDto;
import com.gachonoj.boardservice.domain.dto.response.*;
import com.gachonoj.boardservice.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @DeleteMapping("/admin/notice/{noticeId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteNotice(@PathVariable Long noticeId){
        boardService.deleteNotice(noticeId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 문의사항 작성 API
    @PostMapping("/inquiry")
    public ResponseEntity<CommonResponseDto<Void>> createInquiry(HttpServletRequest request,@RequestBody InquiryRequestDto inquiryRequestDto){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        boardService.createInquiry(inquiryRequestDto, memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 문의사항 수정 API
    @PutMapping("/inquiry/{inquiryId}")
    public ResponseEntity<CommonResponseDto<Void>> updateInquiry(HttpServletRequest request,@PathVariable Long inquiryId,@RequestBody InquiryRequestDto inquiryRequestDto){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        boardService.updateInquiry(inquiryId,inquiryRequestDto, memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 문의사항 삭제 사용자 API
    @DeleteMapping("/inquiry/{inquiryId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteInquiry(HttpServletRequest request,@PathVariable Long inquiryId){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        boardService.deleteInquiryByMember(inquiryId,memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 문의사항 삭제 관리자 API
    @DeleteMapping("/admin/inquiry/{inquiryId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteInquiryByAdmin(@PathVariable Long inquiryId){
        boardService.deleteInquiryByAdmin(inquiryId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }
    // 문의사항 답변 API
    @PostMapping("/admin/inquiry/{inquiryId}/reply")
    public ResponseEntity<CommonResponseDto<Void>> createReply(@PathVariable Long inquiryId, @RequestBody ReplyRequestDto replyRequestDto){
        boardService.createReply(inquiryId,replyRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 메인 대시보드 공지사항 목록 조회 최대 5개
    @GetMapping("/notice/main")
    public ResponseEntity<CommonResponseDto<List<NoticeMainResponseDto>>> getMainNoticeList(){
        List<NoticeMainResponseDto> noticeList = boardService.getMainNoticeList();
        return ResponseEntity.ok(CommonResponseDto.success(noticeList));
    }
    // 공지사항 목록 조회
    @GetMapping("/notice/list")
    public ResponseEntity<CommonResponseDto<Page<NoticeListResponseDto>>> getNoticeList(@RequestParam(required = false,defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(CommonResponseDto.success(boardService.getNoticeList(pageNo)));
    }
    // 공지사항 상세 조회
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<CommonResponseDto<NoticeDetailResponseDto>> getNoticeDetail(@PathVariable Long noticeId){
        NoticeDetailResponseDto noticeDetail = boardService.getNoticeDetail(noticeId);
        return ResponseEntity.ok(CommonResponseDto.success(noticeDetail));
    }
    // 공지사항 목록 조회 관리자
    @GetMapping("/admin/notice/list")
    public ResponseEntity<CommonResponseDto<Page<NoticeListResponseDto>>> getNoticeListAdmin(@RequestParam(required = false,defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(CommonResponseDto.success(boardService.getNoticeList(pageNo)));
    }
    // 문의사항 목록 조회 관리자
    @GetMapping("/admin/inquiry/list")
    public ResponseEntity<CommonResponseDto<Page<InquiryAdminListResponseDto>>> getInquiryListAdmin(@RequestParam(required = false,defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(CommonResponseDto.success(boardService.getInquiryListAdmin(pageNo)));
    }
    // 문의사항 목록 조회 사용자
    @GetMapping("/inquiry/list")
    public ResponseEntity<CommonResponseDto<Page<InquiryListResponseDto>>> getInquiryList(HttpServletRequest request, @RequestParam(required = false,defaultValue = "1") int pageNo) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        return ResponseEntity.ok(CommonResponseDto.success(boardService.getInquiryList(memberId,pageNo)));
    }
    // 문의사항 상세 조회 사용자
    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<CommonResponseDto<InquiryDetailResponseDto>> getInquiryDetail(@PathVariable Long inquiryId){
        InquiryDetailResponseDto inquiryDetail = boardService.getInquiryDetail(inquiryId);
        return ResponseEntity.ok(CommonResponseDto.success(inquiryDetail));
    }
}

