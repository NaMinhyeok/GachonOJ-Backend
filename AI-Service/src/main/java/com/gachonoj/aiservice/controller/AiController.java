package com.gachonoj.aiservice.controller;

import com.gachonoj.aiservice.common.response.CommonResponseDto;
import com.gachonoj.aiservice.domain.dto.request.FeedbackRequestDto;
import com.gachonoj.aiservice.domain.dto.response.AiFeedbackResponseDto;
import com.gachonoj.aiservice.domain.dto.response.TokenUsageResponseDto;
import com.gachonoj.aiservice.service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    // GPT 요청
    @GetMapping("/chat")
    public ResponseEntity<CommonResponseDto<String>> chat(@RequestBody FeedbackRequestDto feedbackRequestDto) {
        String response = aiService.chatGPT(feedbackRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    // GPT 제출한 코드에 대한 피드백 받기
    @GetMapping("/feedback/{submissionId}")
    public ResponseEntity<CommonResponseDto<AiFeedbackResponseDto>> feedback(@PathVariable Long submissionId, HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        AiFeedbackResponseDto response = aiService.feedback(submissionId, memberId);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    // ai 사용 토큰 갯수 조회
    @GetMapping("/admin/token")
    public ResponseEntity<CommonResponseDto<TokenUsageResponseDto>> tokenUsage() {
        TokenUsageResponseDto response = aiService.tokenUsage();
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
}
