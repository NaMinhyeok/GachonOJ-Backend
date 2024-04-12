package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.domain.dto.request.InquiryRequestDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/board")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    // 문의사항 등록
    @PostMapping("/inquiry")
    public ResponseEntity<CommonResponseDto<Inquiry>> createInquiry(@RequestBody InquiryRequestDto inquiryRequestDto) {
        Inquiry inquiry = inquiryService.createInquiry(inquiryRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success(inquiry));
    }

    //문의사항 삭제

    @DeleteMapping("/admin/inquiry/{inquiryId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteInquiry(@PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok(CommonResponseDto.success(null));
    }
}