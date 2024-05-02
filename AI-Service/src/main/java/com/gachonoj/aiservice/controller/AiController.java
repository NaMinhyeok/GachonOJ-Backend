package com.gachonoj.aiservice.controller;

import com.gachonoj.aiservice.common.response.CommonResponseDto;
import com.gachonoj.aiservice.domain.dto.request.FeedbackRequestDto;
import com.gachonoj.aiservice.service.AiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    @GetMapping("/chat")
    public ResponseEntity<CommonResponseDto<String>> chat(FeedbackRequestDto feedbackRequestDto) {
        String response = aiService.chatGPT(feedbackRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
}
