package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.MySubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionRecordResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.TodaySubmissionCountResponseDto;
import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.submissionservice.feign.client.ProblemServiceFeignClient;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionDetailDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionMemberRankInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import com.netflix.discovery.converters.Auto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProblemServiceFeignClient problemServiceFeignClient;
    private final MemberServiceFeignClient memberServiceFeignClient;
    private final SubmissionRepository submissionRepository;
    private final ExecuteService executeService;

    // @Transactional 어노테이션을 사용하기 위해서 Self Injection 사용
    // Self Injection 이 아닌 서비스레이어를 분리해서 사용하도록 하였습니다.
    // Self Injection 을 사용하니 Bean 순환참조에서 문제가 발생하여서 서비스를 분리하였습니다.
    // 다른 방안으로는 서비스를 분리해서 사용하는 방법이 있다.
    // 참조 : https://blog.leaphop.co.kr/blogs/34/Spring__Transaction%EC%9D%84_%EC%9C%84%ED%95%9C_Self_Injection_%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0

    // 문제 코드 실행
    @Transactional
    public List<ExecuteResultResponseDto> executeCodeByProblemId(ExecuteRequestDto executeRequestDto, Long problemId) {
        List<String> input = new ArrayList<>();
        input = problemServiceFeignClient.getVisibleTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getInput)
                .collect(Collectors.toList());
        List<String> output = new ArrayList<>();
        output = problemServiceFeignClient.getVisibleTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getOutput)
                .collect(Collectors.toList());
        // executeRequestDto에서 주는 testcase 추가하기
        if(executeRequestDto.getTestcase()!=null){
            for (Map.Entry<String, String> entry : executeRequestDto.getTestcase().entrySet()) {
                input.add(entry.getKey());
                output.add(entry.getValue());
            }
        }
        Map<String,String> result = executeService.executeCode(executeRequestDto, input,output,10);
        List<ExecuteResultResponseDto> response = new ArrayList<>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            response.add(new ExecuteResultResponseDto(entry.getKey(),entry.getValue()));
        }
        return response;
    }
    // 문제 채점 실행
    @Transactional
    public SubmissionResultResponseDto submissionByProblemId(ExecuteRequestDto executeRequestDto, Long problemId, Long memberId) {
        List<String> input = problemServiceFeignClient.getTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getInput)
                .collect(Collectors.toList());
        List<String> output = problemServiceFeignClient.getTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getOutput)
                .collect(Collectors.toList());
        // 멤버 아이디로 memberRank,needRank,rating 조회
        SubmissionMemberRankInfoResponseDto submissionMemberRankInfoResponseDto = memberServiceFeignClient.getMemberRank(memberId);
        // 문제 아이디로 problemScore 조회
        Integer problemScore = problemServiceFeignClient.getProblemScore(problemId);
        // 문제 time limet 가져오기
        Integer problemTimeLimit = problemServiceFeignClient.getProblemTimeLimit(problemId);


        // 코드 실행 결과
        Map<String,String> result = executeService.executeCode(executeRequestDto, input,output,problemTimeLimit);
        int correctCount = 0;
        // 정답 개수 세기
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if(entry.getValue().equals("정답")){
                correctCount++;
            }
        }
        // 반환하기 위한 변수들
        // isCorrect: 모든 테스트 케이스를 통과했는지 여부
        boolean isCorrect = correctCount==result.size();
        // memberRank: 현재 rank 점수
        Integer memberRank = submissionMemberRankInfoResponseDto.getMemberRank();
        // rating : 현재 rating
        Integer rating = submissionMemberRankInfoResponseDto.getMemberRating();
        // needRating : 다음 rating을 위해 필요한 rank 점수
        Integer needRating = submissionMemberRankInfoResponseDto.getNeedRank();
        Integer afterRating = rating;
        boolean ratingChanged = false;
        // problemScore 가 needRating보다 크면 rating을 +1 해서 afterRating에 저장하고 ratingChaneged를 true로 설정
        if(problemScore>=needRating){
            afterRating++;
            ratingChanged = true;
        }
        // submission 엔티티 생성
        Submission submission = Submission.builder()
                .memberId(memberId)
                .problemId(problemId)
                .submissionCode(executeRequestDto.getCode())
                .submissionStatus(isCorrect ? Status.CORRECT : Status.INCORRECT)
                .submissionLang(Language.fromLabel(executeRequestDto.getLanguage()))
                .build();
        // Member 엔티티에 memberRank 반영
        if(isCorrect){
            memberServiceFeignClient.updateMemberRank(memberId,memberRank+problemScore);
        }
        // Submission 엔티티 저장
        submissionRepository.save(submission);
        // 저장된 submission 엔티티의 id를 반환하기 위해 저장
        Long submissionId = submission.getSubmissionId();
        // 반환
        return new SubmissionResultResponseDto(isCorrect,memberRank,problemScore,memberRank+problemScore,ratingChanged,rating,afterRating,submissionId);
    }

    // 금일 채점 결과 현황 조회
    public TodaySubmissionCountResponseDto getTodaySubmissionCount() {
        log.info("변환된 시간 확인하기" + LocalDate.now().atStartOfDay());
        List<Submission> submissions = submissionRepository.findBySubmissionDateBetween(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(23,59,59));
        int total = submissions.size();
        int correct = 0;
        int incorrect = 0;
        for (Submission submission : submissions) {
            if (submission.getSubmissionStatus() == Status.CORRECT) {
                correct++;
            } else {
                incorrect++;
            }
        }
        return new TodaySubmissionCountResponseDto(total, correct, incorrect);
    }

    // 시험 제출 정보 조회
    public List<SubmissionDetailDto> getSubmissionsDetails(Long memberId, List<Long> problemIds) {
        List<Submission> submissions = submissionRepository.findByMemberIdAndProblemIdIn(memberId, problemIds);
        return submissions.stream()
                .map(submission -> new SubmissionDetailDto(
                        submission.getProblemId(),
                        submission.getSubmissionStatus() == Status.CORRECT,
                        submission.getSubmissionCode()
                ))
                .collect(Collectors.toList());
    }
    // 제출한 코드 확인하기
    public MySubmissionResultResponseDto getSubmissionCodeBySubmissionId(Long submissionId) {
        Submission submission = submissionRepository.findBySubmissionId(submissionId);
        // 문제 제목 가져오기
        String problemTitle = problemServiceFeignClient.getProblemTitle(submission.getProblemId());
        // 닉네임 가져오기
        String nickname = memberServiceFeignClient.getMemberNickname(submission.getMemberId());
        return new MySubmissionResultResponseDto(nickname,problemTitle,submission.getSubmissionCode());
    }

    // 제출 이력 조회
    public List<SubmissionRecordResponseDto> getSubmissionRecordsByMemberAndProblemId(Long memberId, Long problemId) {
        List<Submission> submissions = submissionRepository.findByMemberIdAndProblemId(memberId, problemId);
        return submissions.stream()
                .map(submission -> new SubmissionRecordResponseDto(
                        submission.getSubmissionId(),
                        submission.getSubmissionStatus().getLabel(),
                        submission.getSubmissionLang(),
                        changeTimeFormat(submission.getSubmissionDate())
                ))
                .toList();
    }
    // 시간 포맷 변경 함수(YYYY-MM-MM HH:MM:SS)
    public String changeTimeFormat(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
