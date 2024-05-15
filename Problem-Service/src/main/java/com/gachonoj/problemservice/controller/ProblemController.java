package com.gachonoj.problemservice.controller;

import com.gachonoj.problemservice.common.codes.ErrorCode;
import com.gachonoj.problemservice.common.response.CommonResponseDto;
import com.gachonoj.problemservice.domain.dto.request.ExamRequestDto;
import com.gachonoj.problemservice.domain.dto.request.ProblemRequestDto;
import com.gachonoj.problemservice.domain.dto.response.*;
import com.gachonoj.problemservice.service.ExamService;
import com.gachonoj.problemservice.service.ProblemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Lombok 어노테이션 - 코드 간결하게 작성
@Slf4j  // Slf4j를 이용한 간편 로깅
@RestController // Restful 웹 서비스 컨트롤러
@RequiredArgsConstructor    // 필수 final 필드 주입 생성자 생성
@RequestMapping("/problem")   // 이 컨트롤러의 모든 메서드에 대한 기본 경로
public class ProblemController {
    private final ProblemService problemService;    // 주입된 ProblemService 의존성
    private final ExamService examService;

    // 시험 등록
    @PostMapping("/exam/register")
    public ResponseEntity<CommonResponseDto<Void>> registerExam(@RequestBody ExamRequestDto examDto, HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        examService.registerExam(examDto, memberId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 시험 수정
    @PutMapping("/exam/{examId}")
    public ResponseEntity<CommonResponseDto<Void>> updateExam(
            @PathVariable Long examId,  // 시험 ID는 URL 매개변수로 받는다
            @RequestBody ExamRequestDto examDto,  // 수정할 시험정보는 Request Body에서 받는다
            HttpServletRequest request) {

        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));  // 회원 ID를 헤더에서 추출
        examService.updateExam(examId, examDto);  // 서비스 레이어에 업데이트 로직 위임
        return ResponseEntity.ok(CommonResponseDto.success());  // 성공 응답
    }

    // 시험 문제 조회
    @GetMapping("/exam/{examId}")
    public ResponseEntity<CommonResponseDto<ExamDetailResponseDto>> getExamDetail(@PathVariable Long examId) {
        ExamDetailResponseDto examDetail = examService.getExamDetail(examId);
        return ResponseEntity.ok(CommonResponseDto.success(examDetail));  // 성공 응답
    }

    // 시험 삭제
    @DeleteMapping("/exam/{examId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteExam(@PathVariable Long examId, HttpServletRequest request) {
        String memberIdStr = request.getHeader("X-Authorization-Id");
        long memberId;
        try {
            memberId = Long.parseLong(memberIdStr);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponseDto.fail(ErrorCode.BAD_REQUEST_ERROR, "Invalid member ID"));
        }

        try {
            examService.deleteExam(examId, memberId);
            return ResponseEntity.ok(CommonResponseDto.success());
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CommonResponseDto.fail(ErrorCode.FORBIDDEN_ERROR, "Access denied"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error"));
        }
    }
    // 알고리즘 문제 등록
    @PostMapping("/admin/register")
    public ResponseEntity<CommonResponseDto<Void>> registerProblem(@RequestBody ProblemRequestDto problemRequestDto) {
        problemService.registerProblem(problemRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 알고리즘 문제 수정
    @PutMapping("/admin/register/{problemId}")
    public ResponseEntity<CommonResponseDto<Void>> updateProblem(@PathVariable Long problemId, @RequestBody ProblemRequestDto problemRequestDto) {
        problemService.updateProblem(problemId, problemRequestDto);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 알고리즘 문제 삭제
    @DeleteMapping("/admin/{problemId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteProblem(@PathVariable Long problemId) {
        problemService.deleteProblem(problemId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

//    @GetMapping("/bookmark/list")
//    public ResponseEntity<CommonResponseDto<Page<BookmarkProblemResponseDto>>> getBookmarkProblemList(
//            @RequestParam(defaultValue = "1") int pageNo, HttpServletRequest request) {
//        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
//        // 강제로 회원 ID를 받아 사용하거나, 인증 정보에서 회원 ID를 추출해야 할 경우 로직 추가
//        Page<BookmarkProblemResponseDto> result = problemService.getBookmarkProblemList(memberId, pageNo);
//        return ResponseEntity.ok(CommonResponseDto.success(result));
//    }

    // 북마크 등록
    @PostMapping("/bookmark/{problemId}")
    public ResponseEntity<CommonResponseDto<Void>> addBookmark(@PathVariable Long problemId, HttpServletRequest request){
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        problemService.addBookmark(memberId, problemId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

    // 북마크 삭제
    @DeleteMapping("/bookmark/{problemId}")
    public ResponseEntity<CommonResponseDto<Void>> removeBookmark(@PathVariable Long problemId, HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        problemService.removeBookmark(memberId, problemId);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

//    @GetMapping("/challenging/list")
//    public ResponseEntity<CommonResponseDto<Page<WrongProblemResponseDto>>> getIncorrectProblemList(
//            @RequestParam(defaultValue = "1") int pageNo, HttpServletRequest request) {
//        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
//        // 강제로 회원 ID를 받아 사용하거나, 인증 정보에서 회원 ID를 추출해야 할 경우 로직 추가
//        Page<WrongProblemResponseDto> result = problemService.getIncorrectProblemList(memberId, pageNo);
//        return ResponseEntity.ok(CommonResponseDto.success(result));
//    }

//    @GetMapping("/solved/list")
//    public ResponseEntity<CommonResponseDto<Page<SolvedProblemResponseDto>>> getSolvedProblemList(
//            @RequestParam(defaultValue = "1") int pageNo, HttpServletRequest request) {
//        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
//        // 강제로 회원 ID를 받아 사용하거나, 인증 정보에서 회원 ID를 추출해야 할 경우 로직 추가
//        Page<SolvedProblemResponseDto> result = problemService.getSolvedProblemList(memberId, pageNo);
//        return ResponseEntity.ok(CommonResponseDto.success(result));
//    }

    // 참가 대회&시험 예정 조회
    @GetMapping("/exam/scheduled")
    public ResponseEntity<CommonResponseDto<List<ScheduledContestResponseDto>>> getScheduledContests(@RequestHeader("X-Authorization-Id") String authorizationId,
                                                                                                     @RequestParam(required = false) String type) {
        Long memberId = Long.parseLong(authorizationId);
        List<ScheduledContestResponseDto> result = examService.getScheduledContests(memberId,type);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // 지난 대회&시험 조회
    @GetMapping("/exam/past")
    public ResponseEntity<CommonResponseDto<List<PastContestResponseDto>>> getPastContests(@RequestHeader("X-Authorization-Id") String authorizationId,
                                                                                           @RequestParam(required = false) String type){
        Long memberId = Long.parseLong(authorizationId);
        List<PastContestResponseDto> result = examService.getPastContests(memberId,type);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // 문제 목록 조회
    @GetMapping("/problems/list")
    public ResponseEntity<CommonResponseDto<Page<ProblemListResponseDto>>> getProblemList(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                          @RequestParam(required = false) String search,
                                                                                          @RequestParam(required = false) String classType,
                                                                                          @RequestParam(required = false) Integer diff,
                                                                                          @RequestParam(required = false) String sortType) {
        Page<ProblemListResponseDto> result = problemService.getProblemList(pageNo, search, classType, diff, sortType);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 추천 알고리즘 문제 조회
    @GetMapping("/problem/recommend")
    public ResponseEntity<CommonResponseDto<List<RecommendProblemResponseDto>>> getRecommendProblems() {
        List<RecommendProblemResponseDto> result = problemService.getRecommenedProblemList();
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 교수님 시험 목록 조회
    @GetMapping("/exam/professor/list")
    public ResponseEntity<CommonResponseDto<Page<ProfessorExamListResponseDto>>> getProfessorExamList(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                                      HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        Page<ProfessorExamListResponseDto> result = examService.getProfessorExamList(memberId, pageNo);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 관리자 시험 또는 대회 목록 조회
    @GetMapping("/admin/exam/list")
    public ResponseEntity<CommonResponseDto<Page<ExamOrContestListResponseDto>>> getExamOrContestList(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                                      @RequestParam String type) {
        Page<ExamOrContestListResponseDto> result = examService.getExamOrContestList(pageNo,type);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 관리자 문제 목록 조회
    @GetMapping("/admin/problems/list")
    public ResponseEntity<CommonResponseDto<Page<ProblemListByAdminResponseDto>>> getProblemListByAdmin(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                                        @RequestParam(required = false) String search) {
        Page<ProblemListByAdminResponseDto> result = problemService.getProblemListByAdmin(pageNo,search);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 사용자 문제 목록 조회
    @GetMapping("/problems/list/member")
    public ResponseEntity<CommonResponseDto<Page<ProblemListResponseDto>>> getProblemListByMember(@RequestParam(required = false,defaultValue = "1") int pageNo,
                                                                                                  @RequestParam(required = false) String type,
                                                                                                  @RequestParam(required = false) String search,
                                                                                                  @RequestParam(required = false) String classType,
                                                                                                  @RequestParam(required = false) Integer diff,
                                                                                                  @RequestParam(required = false) String sortType,
                                                                                                  HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        Page<ProblemListResponseDto> result = problemService.getProblemListByMember(type,pageNo, search, classType, diff, sortType, memberId);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }
    // 문제 응시 화면 문제 상세 조회
    @GetMapping("/problems/{problemId}")
    public ResponseEntity<CommonResponseDto<ProblemDetailResponseDto>> getProblemDetail(@PathVariable Long problemId) {
        ProblemDetailResponseDto result = problemService.getProblemDetail(problemId);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // 시험 또는 대회 대기 화면
    @GetMapping("/exam/info/{examId}")
    public ResponseEntity<CommonResponseDto<ExamOrContestInfoResponseDto>> getExamOrContestInfo(@PathVariable Long examId,
                                                                                       @RequestParam String type) {
        ExamOrContestInfoResponseDto result = examService.getExamOrContestInfo(examId, type);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // 문제 수정시 문제 상세 조회
    @GetMapping("/admin/register/{problemId}")
    public ResponseEntity<CommonResponseDto<ProblemDetailAdminResponseDto>> getProblemDetailAdmin(@PathVariable Long problemId) {
        ProblemDetailAdminResponseDto result = problemService.getProblemDetailAdmin(problemId);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // 시험 결과 상세 조회
    @GetMapping("/admin/result/{testId}")
    public ResponseEntity<CommonResponseDto<ExamResultDetailsResponseDto>> getExamResults(@PathVariable Long testId) {
            ExamResultDetailsResponseDto examResult = examService.getExamResults(testId);
            return ResponseEntity.ok(CommonResponseDto.success(examResult));
    }

    // 시험 결과 목록 조회
    @GetMapping("/exam/{examId}/results")
    public ResponseEntity<CommonResponseDto<Page<ExamResultListDto>>> getExamResultList(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "1") int page) {
        Page<ExamResultListDto> results = examService.getExamResultList(examId, page);
        return ResponseEntity.ok(CommonResponseDto.success(results));
    }

    // 교수 대시보드 진행중인 시험 목록 조회
    @GetMapping("/professor/exam/ongoing")
    public ResponseEntity<CommonResponseDto<List<ExamCardInfoResponseDto>>> getOngoingExam(HttpServletRequest request) {
        Long memberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        List<ExamCardInfoResponseDto> result = examService.getProfessorExamCardInfo(memberId);
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

    // TODO : 교수 대시보드 최근 오답률 높은 문제 분류 TOP 3 서비스 로직 만들기
    // 교수 대시보드 최근 오답률 높은 문제 분류 TOP 3
//    @GetMapping("/professor/incorrect/class")
//    public ResponseEntity<CommonResponseDto<List<ProblemCardResponseDto>>> getIncorrectProblemClassList() {
//        List<ProblemCardResponseDto> result = problemService.getTop3IncorrectProblemClass();
//        return ResponseEntity.ok(CommonResponseDto.success(result));
//    }

    // 교수 대시보드 오답률 높은 알고리즘 TOP 5
    @GetMapping("/professor/incorrect")
    public ResponseEntity<CommonResponseDto<List<ProblemCardResponseDto>>> getIncorrectProblemList() {
        List<ProblemCardResponseDto> result = problemService.getTop5IncorrectProblem();
        return ResponseEntity.ok(CommonResponseDto.success(result));
    }

}