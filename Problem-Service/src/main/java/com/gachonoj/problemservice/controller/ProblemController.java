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
@RequestMapping("/api/problem")   // 이 컨트롤러의 모든 메서드에 대한 기본 경로
public class ProblemController {
    private final ProblemService problemService;    // 주입된 ProblemService 의존성
    /*
     * feign Client를 이용한 API 작성
     * */
    @GetMapping("/member/bookmark")
    public Integer getBookmarkCountByMemberId(@RequestParam Long memberId) {
        return problemService.getBookmarkCountByMemberId(memberId);
    }
    /*
     * 문제 서비스 자체적으로 사용하는 API 작성
     * */

    //알고리즘 문제 등록
    @PostMapping("/admin/register")
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

    // 알고리즘 문제 수정
    @PutMapping("/admin/register/{problemId}")
    public ResponseEntity<Map<String, Object>> updateProblem(@PathVariable Long problemId, @RequestBody ProblemRequestDto problemRequestDto) {
        // ProblemService를 호출하여 요청 DTO에 기반하여 문제 수정
        problemService.updateProblem(problemId, problemRequestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code", HttpStatus.OK.value()); // 200
        response.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // 현재 시간
        response.put("msg", "문제 수정 성공");
        // 문제 수정 성공 응답에는 수정된 문제의 세부 정보를 반환하지 않음

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 알고리즘 문제 삭제
    @DeleteMapping("/admin/{problemId}")
    public ResponseEntity<Map<String, Object>> deleteProblem(@PathVariable Long problemId) {
        problemService.deleteProblem(problemId);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code", HttpStatus.OK.value());
        response.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("msg", "문제 삭제 성공");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}