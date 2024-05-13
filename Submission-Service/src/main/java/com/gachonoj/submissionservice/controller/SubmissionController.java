package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.common.response.CommonResponseDto;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionResultResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionDetailDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionExamResultInfoResponseDto;
import com.gachonoj.submissionservice.service.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<CommonResponseDto<List<ExecuteResultResponseDto>>>  executeCode(@PathVariable Long problemId, @RequestBody ExecuteRequestDto executeRequestDto) {
        List<ExecuteResultResponseDto> response = submissionService.executeCodeByProblemId(executeRequestDto, problemId);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    @PostMapping("/submit/{problemId}")
    public ResponseEntity<CommonResponseDto<SubmissionResultResponseDto>> submitCode(@PathVariable Long problemId, @RequestBody ExecuteRequestDto executeRequestDto, HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        SubmissionResultResponseDto response = submissionService.submissionByProblemId(executeRequestDto, problemId, memberId);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

    @GetMapping("/results")
    public ResponseEntity<CommonResponseDto<SubmissionExamResultInfoResponseDto>> getSubmissionsInfo(
            @RequestParam List<Long> problemIds,
            @RequestParam Long memberId) {
        List<SubmissionDetailDto> submissionDetails = submissionService.getSubmissionsDetails(memberId, problemIds);
        SubmissionExamResultInfoResponseDto responseDto = new SubmissionExamResultInfoResponseDto(submissionDetails);
        return ResponseEntity.ok(CommonResponseDto.success(responseDto));
    }
}
