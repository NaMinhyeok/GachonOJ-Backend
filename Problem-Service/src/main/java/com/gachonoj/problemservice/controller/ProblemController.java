package com.gachonoj.problemservice.controller;

import com.gachonoj.problemservice.common.response.CommonResponseDto;
import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.entity.Problem;
import com.gachonoj.problemservice.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Lombok 어노테이션 - 코드 간결하게 작성
@Slf4j  // Slf4j를 이용한 간편 로깅
@RestController // Restful 웹 서비스 컨트롤러
@RequiredArgsConstructor    // 필수 final 필드 주입 생성자 생성
@RequestMapping("/problem")   // 이 컨트롤러의 모든 메서드에 대한 기본 경로
public class ProblemController {
    @Autowired
    private ProblemService problemService;    // 주입된 ProblemService 의존성

    //알고리즘 문제 등록
    @PostMapping("/admin/register")
    public ResponseEntity<CommonResponseDto<Long>> registerProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        // ProblemService를 호출하여 요청 DTO에 기반하여 문제 등록
        Long problemId = problemService.registerProblem(problemRequestDto);
        // 성공적인 생성이 생성된 엔터티의 ID를 반환하는 패턴
        return ResponseEntity.ok(CommonResponseDto.success());
        // API 디자인 패턴: 성공적인 생성이 생성된 엔터티의 세부 정보를 반환하지 않는 패턴
    }

    // 알고리즘 문제 수정
    @PutMapping("/admin/register/{problemId}")
    public ResponseEntity<CommonResponseDto<Long>> updateProblem(@PathVariable Long problemId, @RequestBody ProblemRequestDto problemRequestDto) {
        // 문제 수정
        Long problem = problemService.updateProblem(problemId, problemRequestDto);

        // 수정된 문제의 세부 정보를 포함하여 성공 응답 반환
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 알고리즘 문제 삭제
    @DeleteMapping("/admin/{problemId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteProblem(@PathVariable Long problemId) {
        // 문제 삭제 요청 처리
        problemService.deleteProblem(problemId);

        // 문제 삭제 성공 메시지와 함께 응답 반환
        return ResponseEntity.ok(CommonResponseDto.success());
    }
}