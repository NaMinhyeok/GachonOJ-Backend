package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.common.codes.SuccessCode;
import com.gachonoj.boardservice.domain.dto.InquiryRequestDto;
import com.gachonoj.boardservice.domain.entity.Inquiry;
import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteInquiry(@PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        // SuccessCode를 사용하여 DELETE 성공 응답 설정
        return ResponseEntity.ok(CommonResponseDto.<Void>builder()
                .isSuccess(true)
                .code(SuccessCode.DELETE_SUCCESS.getStatus())
                .timestamp(LocalDateTime.now())
                .message(SuccessCode.DELETE_SUCCESS.getMessage())
                .result(null) // 결과가 없기 때문에 null
                .build());
    }
}