package com.gachonoj.aiservice.service;

import com.gachonoj.aiservice.domain.dto.request.ChatGPTRequest;
import com.gachonoj.aiservice.domain.dto.request.FeedbackRequestDto;
import com.gachonoj.aiservice.domain.dto.response.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.url}")
    private String url;
    private final RestTemplate restTemplate;

    public String chatGPT(FeedbackRequestDto feedbackRequestDto) {
        String code = feedbackRequestDto.getCode();
        String prompt = feedbackRequestDto.getPrompt();
        prompt = code + "\n" + prompt;
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse response = restTemplate.postForObject(url, request, ChatGPTResponse.class);
        log.info(response.toString());
        return response.getChoices().get(0).getMessage().getContent();
    }

}
