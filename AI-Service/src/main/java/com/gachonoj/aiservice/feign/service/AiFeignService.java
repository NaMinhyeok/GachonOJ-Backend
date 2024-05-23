package com.gachonoj.aiservice.feign.service;

import com.gachonoj.aiservice.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFeignService {

    private final FeedbackRepository feedbackRepository;

    //memberId 전송해서 해당 memberId를 외래키로 사용하고있다면 삭제하도록 한다.
    @Transactional
    public void deleteAiByMemberId(Long memberId) {
        feedbackRepository.deleteByMemberId(memberId);
    }
}
