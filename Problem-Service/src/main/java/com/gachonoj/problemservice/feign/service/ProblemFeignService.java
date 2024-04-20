package com.gachonoj.problemservice.feign.service;

import com.gachonoj.problemservice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemFeignService {

    private final ProblemRepository problemRepository;

    // 북마크 갯수 조회
    public Integer getBookmarkCountByMemberId(Long memberId) {
        return problemRepository.getBookmarkCountByMemberId(memberId);
    }

    // 정답자 수 조회
}
