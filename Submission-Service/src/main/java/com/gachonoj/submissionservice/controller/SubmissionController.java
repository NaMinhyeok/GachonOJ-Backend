package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    // 코드 실행
     @PostMapping("/execute/{problemId}")
     public Void executeCode(@PathVariable Long problemId, @RequestBody ExecuteTestRequestDto executeTestRequestDto) {
         return submissionService.executeCodeByProblemId(executeTestRequestDto,problemId);
     }
}
