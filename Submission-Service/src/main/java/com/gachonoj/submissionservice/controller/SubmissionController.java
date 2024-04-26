package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.common.response.CommonResponseDto;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteRsultResponseDto;
import com.gachonoj.submissionservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    // 코드 실행
     @PostMapping("/execute/{problemId}")
     public ResponseEntity<CommonResponseDto<List<ExecuteRsultResponseDto>>>  executeCode(@PathVariable Long problemId, @RequestBody ExecuteTestRequestDto executeTestRequestDto) {
         List<ExecuteRsultResponseDto> response = submissionService.executeCodeByProblemId(executeTestRequestDto, problemId);
         return ResponseEntity.ok(CommonResponseDto.success(response));
     }
}
