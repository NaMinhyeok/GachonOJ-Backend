package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.TodaySubmissionCountResponseDto;
import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.submissionservice.feign.client.ProblemServiceFeignClient;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionMemberRankInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import com.netflix.discovery.converters.Auto;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProblemServiceFeignClient problemServiceFeignClient;
    private final MemberServiceFeignClient memberServiceFeignClient;
    private final SubmissionRepository submissionRepository;

    // @Transactional 어노테이션을 사용하기 위해서 Self Injection 사용
    // 다른 방안으로는 서비스를 분리해서 사용하는 방법이 있다.
    // 참조 : https://blog.leaphop.co.kr/blogs/34/Spring__Transaction%EC%9D%84_%EC%9C%84%ED%95%9C_Self_Injection_%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0
    private SubmissionService self;

    @Autowired
    public void setSelf(SubmissionService self) {
        this.self = self;
    }

    // 문제 코드 실행
    @Transactional
    public List<ExecuteResultResponseDto> executeCodeByProblemId(ExecuteRequestDto executeRequestDto, Long problemId) {
        List<String> input = problemServiceFeignClient.getVisibleTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getInput)
                .toList();
        List<String> output = problemServiceFeignClient.getVisibleTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getOutput)
                .toList();
        // executeRequestDto에서 주는 testcase 추가하기
        if(executeRequestDto.getTestcase()!=null){
            for (Map.Entry<String, String> entry : executeRequestDto.getTestcase().entrySet()) {
                input.add(entry.getKey());
                output.add(entry.getValue());
            }
        }
        Map<String,String> result = self.executeCode(executeRequestDto, input,output,10);
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
                .toList();
        List<String> output = problemServiceFeignClient.getTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getOutput)
                .toList();
        // 멤버 아이디로 memberRank,needRank,rating 조회
        SubmissionMemberRankInfoResponseDto submissionMemberRankInfoResponseDto = memberServiceFeignClient.getMemberRank(memberId);
        // 문제 아이디로 problemScore 조회
        Integer problemScore = problemServiceFeignClient.getProblemScore(problemId);
        // 문제 time limet 가져오기
        Integer problemTimeLimit = problemServiceFeignClient.getProblemTimeLimit(problemId);


        // 코드 실행 결과
        Map<String,String> result = self.executeCode(executeRequestDto, input,output,problemTimeLimit);
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

    // 코드 실행하는 메소드
    @Transactional
    public Map<String,String> executeCode(ExecuteRequestDto executeRequestDto, List<String> inputList, List<String> outputList,Integer timeLimit) {
        try {
            Map<String,String> result = new HashMap<>();
            // /home/exec 디렉토리 생성
            Path execDir = Paths.get("/home/exec");
            Files.createDirectories(execDir);
            log.info("Directory created");

            // /home/exec 디렉토리에 Main.java 파일 저장
            Path filePath = Paths.get(execDir.toString(), "Main." + executeRequestDto.getLanguage());
            Files.write(filePath, executeRequestDto.getCode().getBytes());
            log.info("Code saved");

            // 컴파일
            ProcessBuilder compileProcessBuilder = switch (executeRequestDto.getLanguage()) {
                case "Java" -> new ProcessBuilder("javac", "Main.java");
                case "C++" -> new ProcessBuilder("g++", "-o", "Main", "Main.cpp");
                case "C" -> new ProcessBuilder("gcc", "-o", "Main", "Main.c");
                case "Python", "JavaScript" -> // Python,JavaScript는 컴파일이 필요 없습니다.
                        null;
                default ->
                        throw new IllegalArgumentException("Unsupported language: " + executeRequestDto.getLanguage());
            };
            if (compileProcessBuilder != null) {
                compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
                Process compileProcess = compileProcessBuilder.start();
                compileProcess.waitFor();
                log.info("Code compiled");
            }
            for(int i=0; i<inputList.size(); i++){
                // 실행
                ProcessBuilder runProcessBuilder = switch (executeRequestDto.getLanguage()) {
                    case "Java" -> new ProcessBuilder("java", "Main");
                    case "C++", "C" -> new ProcessBuilder("./Main");
                    case "Python" -> new ProcessBuilder("python3", "Main.py");
                    case "JavaScript" -> new ProcessBuilder("node", "Main.js");
                    default ->
                            throw new IllegalArgumentException("Unsupported language: " + executeRequestDto.getLanguage());
                };
                runProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정

                long startTime = System.nanoTime(); // 시작 시간
                Process runProcess = runProcessBuilder.start();
                log.info("Code executed");

                // 입력 제공
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
                writer.write(inputList.get(i));
                writer.flush();
                writer.close();

                // 결과 가져오기
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));

                StringBuilder outputResult = new StringBuilder();
                String line;
                List<String> lines = new ArrayList<>();
                while ((line = stdInput.readLine()) != null) {
                    lines.add(line);
                }

                for (int j = 0; j < lines.size(); j++) {
                    outputResult.append(lines.get(j));
                    if (j < lines.size() - 1) {
                        outputResult.append("\n");
                    }
                }
                StringBuilder errorResult = new StringBuilder();
                while ((line = stdError.readLine()) != null) {
                    errorResult.append(line).append("\n");
                }

                // 에러가 발생한 경우 에러 메시지를 result에 저장하고 반복문을 중단합니다.
                if (errorResult.length() > 0) {
                    log.info("Code error: " + i + "번째" + errorResult.toString());
                    result.put(errorResult.toString(),"에러");
                    break;
                }

                long endTime = System.nanoTime(); // 종료 시간
                long timeElapsed = endTime - startTime; // 소요시간
                // 시간 초과인 경우 에러 메시지를 result에 저장하고 반복문을 중단합니다.
                if (timeElapsed / 1000000000 > timeLimit) {
                    log.info("Time limit exceeded: " + timeElapsed);
                    result.put("Time limit exceeded","시간초과");
                    break;
                }
                log.info("Execution time in nanoseconds: " + timeElapsed);
                log.info("Execution time in milliseconds: " + timeElapsed / 1000000);
                log.info("Code output: " + i + "번째" + outputResult.toString());
                // 정답인지 확인
                if(outputList.get(i).equals(outputResult.toString())){
                    log.info("output: " + outputList.get(i) + "정답: " + outputResult.toString());
                    result.put(outputResult.toString(),"정답");
                }else{
                    log.info("output: " + outputList.get(i) + "오답: " + outputResult.toString());
                    result.put(outputResult.toString(),"오답");
                }
            }

            // /home/exec 디렉토리 삭제
            Files.walk(execDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            log.info("Directory deleted");

            return result;
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            Map<String, String> result = new HashMap<>();
            result.put(e.getMessage(), "Error");
            return result;
        }
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
}
