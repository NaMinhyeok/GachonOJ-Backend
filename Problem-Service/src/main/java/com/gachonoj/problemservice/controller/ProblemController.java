package com.gachonoj.problemservice.controller;

import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/admin/problem")   // 이 컨트롤러의 모든 메서드에 대한 기본 경로
public class ProblemController {
    private final ProblemService problemService;    // 주입된 ProblemService 의존성
    //알고리즘 문제 등록
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        // ProblemService를 호출하여 요청 DTO에 기반하여 문제 등록
        problemService.registerProblem(problemRequestDto); // 문제 ID 저장하지 않음

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code", HttpStatus.CREATED.value()); // 201
        response.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // 현재 시간
        response.put("msg", "문제 등록 성공");
        // API 디자인 패턴: 성공적인 생성이 생성된 엔터티의 세부 정보를 반환하지 않는 패턴

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}