package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.domain.dto.InquiryRequestDto;
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

    @PostMapping("/inquiry")
    public ResponseEntity<CommonResponseDto<Inquiry>> createInquiry(@RequestBody InquiryRequestDto inquiryRequestDto) {
        Inquiry inquiry = inquiryService.createInquiry(inquiryRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success(inquiry));
    }
}