package com.gachonoj.boardservice.controller;

import com.gachonoj.boardservice.domain.dto.ReplyRequestDto;
import com.gachonoj.boardservice.domain.dto.ReplyResponseDto;
import com.gachonoj.boardservice.common.response.CommonResponseDto;
import com.gachonoj.boardservice.domain.entity.Reply;
import com.gachonoj.boardservice.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board/admin")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @PostMapping("/{inquiryId}/inquiry/reply")
    public ResponseEntity<CommonResponseDto<ReplyResponseDto>> createReply(@PathVariable Long inquiryId, @RequestBody ReplyRequestDto replyRequestDto) {
        Reply reply = replyService.createReply(inquiryId, replyRequestDto);
        ReplyResponseDto responseDto = new ReplyResponseDto(reply);
        return ResponseEntity.ok(CommonResponseDto.success(responseDto));
    }
}