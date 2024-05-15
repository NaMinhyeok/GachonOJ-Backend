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

    @GetMapping("/chat")
    public ResponseEntity<CommonResponseDto<String>> chat(@RequestBody FeedbackRequestDto feedbackRequestDto) {
        String response = aiService.chatGPT(feedbackRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    // TODO : 문제번호를 받아서 해당 문제에 대한 제출 코드를 가져오고 그 코드를 AI에 넣어서 결과를 받아온다.
    //        그 결과를 다시 제출자에게 전달한다.
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
