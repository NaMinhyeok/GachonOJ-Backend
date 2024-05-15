package com.gachonoj.aiservice.service;

import com.gachonoj.aiservice.domain.dto.request.ChatGPTRequest;
import com.gachonoj.aiservice.domain.dto.request.FeedbackRequestDto;
import com.gachonoj.aiservice.domain.dto.response.AiFeedbackResponseDto;
import com.gachonoj.aiservice.domain.dto.response.ChatGPTResponse;
import com.gachonoj.aiservice.domain.dto.response.TokenUsageResponseDto;
import com.gachonoj.aiservice.domain.entity.Feedback;
import com.gachonoj.aiservice.feign.client.ProblemServiceFeignClient;
import com.gachonoj.aiservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.aiservice.feign.dto.response.SubmissionCodeInfoResponseDto;
import com.gachonoj.aiservice.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    @Value("${openai.api.model}")
    private String model;
    @Value("${openai.api.url}")
    private String url;
    private final RestTemplate restTemplate;

    private final SubmissionServiceFeignClient submissionServiceFeignClient;
    private final ProblemServiceFeignClient problemServiceFeignClient;
    private final FeedbackRepository feedbackRepository;

    public String chatGPT(FeedbackRequestDto feedbackRequestDto) {
        String code = feedbackRequestDto.getCode();
        String prompt = feedbackRequestDto.getPrompt();
        prompt = code + "\n" + prompt;
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse response = restTemplate.postForObject(url, request, ChatGPTResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
    // TODO : 문제번호를 받아서 해당 문제에 대한 제출 코드를 가져오고 그 코드를 AI에 넣어서 결과를 받아온다.
    //        그 결과를 다시 제출자에게 전달한다.
    public AiFeedbackResponseDto feedback(Long submissionId, Long memberId) {
        // 제출 아이디 통해서 제출 코드 가져오기
        // 제출 아이디 통해서 문제 번호도 가져오기
        // 문제 번호 통해서 프롬프트 가져오기
        // 제출 코드와 프롬프트를 합쳐서 AI에 넣기
        // 결과 받아오기
        // submissionId, problemId, memberId, aiContents, totalTokens 를 Feedback 객체에 저장
        // 결과를 제출자에게 전달
        SubmissionCodeInfoResponseDto submissionCodeInfoResponseDto = submissionServiceFeignClient.getSubmissionCodeBySubmissionId(submissionId);
        String code = submissionCodeInfoResponseDto.getCode();
        log.info("code : {}", code);
        Long problemId = submissionCodeInfoResponseDto.getProblemId();
        log.info("problemId : {}", problemId);
        String prompt = problemServiceFeignClient.getProblemPromptByProblemId(problemId);
        String aiPrompt = code + "\n" + prompt;
        ChatGPTRequest request = new ChatGPTRequest(model, aiPrompt);
        ChatGPTResponse response = restTemplate.postForObject(url, request, ChatGPTResponse.class);
        String aiContents = response.getChoices().get(0).getMessage().getContent();
        Integer totalTokens = response.getUsage().getTotal_tokens();
        Feedback feedback = new Feedback(submissionId, memberId, problemId, aiContents,totalTokens);
        feedbackRepository.save(feedback);
        return new AiFeedbackResponseDto(aiContents);
    }
    // 토큰 사용량 조회
    public TokenUsageResponseDto tokenUsage() {
        List<Feedback> feedbacks= feedbackRepository.findByFeedbackCreatedDateBetween(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(23,59,59));
        Long todayTokenUsage = feedbacks.stream().mapToLong(Feedback::getTotalTokens).sum();
        Long totalTokenUsage = feedbackRepository.findAll().stream().mapToLong(Feedback::getTotalTokens).sum();
        return new TokenUsageResponseDto(todayTokenUsage, totalTokenUsage);
    }

}
