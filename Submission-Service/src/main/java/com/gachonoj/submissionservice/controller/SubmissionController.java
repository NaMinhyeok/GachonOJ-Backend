package com.gachonoj.submissionservice.controller;

import com.gachonoj.submissionservice.common.response.CommonResponseDto;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.MySubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.TodaySubmissionCountResponseDto;
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
    public ResponseEntity<CommonResponseDto<SubmissionResultResponseDto>> submitCode(HttpServletRequest request, @PathVariable Long problemId, @RequestBody ExecuteRequestDto executeRequestDto) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        SubmissionResultResponseDto response = submissionService.submissionByProblemId(executeRequestDto, problemId, memberId);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    // 금일 채점 결과 현황 조회
    @GetMapping("/admin/today")
    public ResponseEntity<CommonResponseDto<TodaySubmissionCountResponseDto>> getTodaySubmissionCount() {
        TodaySubmissionCountResponseDto response = submissionService.getTodaySubmissionCount();
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }
    // 나의 제출 코드 조회
    @GetMapping("/result/{submissionId}")
    public ResponseEntity<CommonResponseDto<MySubmissionResultResponseDto>> getSubmissionResult(@PathVariable Long submissionId) {
        MySubmissionResultResponseDto response = submissionService.getSubmissionCodeBySubmissionId(submissionId);
        return ResponseEntity.ok(CommonResponseDto.success(response));
    }

}
