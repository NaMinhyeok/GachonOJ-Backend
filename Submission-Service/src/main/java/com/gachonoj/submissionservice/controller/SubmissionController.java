package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    // 코드 실행
     @PostMapping("/execute")
     public String executeCode(@RequestBody ExecuteTestRequestDto executeTestRequestDto) {
         return submissionService.executeCode(executeTestRequestDto);
     }
}
