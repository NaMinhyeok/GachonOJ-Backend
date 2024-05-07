package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.constant.Language;
import com.gachonoj.submissionservice.domain.constant.Status;
import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteResultResponseDto;
import com.gachonoj.submissionservice.domain.dto.response.SubmissionResultResponseDto;
import com.gachonoj.submissionservice.domain.entity.Submission;
import com.gachonoj.submissionservice.feign.client.MemberServiceFeignClient;
import com.gachonoj.submissionservice.feign.client.ProblemServiceFeignClient;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionMemberRankInfoResponseDto;
import com.gachonoj.submissionservice.feign.dto.response.SubmissionProblemTestCaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProblemServiceFeignClient problemServiceFeignClient;
    private final MemberServiceFeignClient memberServiceFeignClient;

    // 문제 코드 실행
    // TODO : 문제의 테스트케이스 추가된 경우 가져와서 실행하도록 한다.
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
        Map<String,String> result = executeCode(executeRequestDto, input,output,10);
        List<ExecuteResultResponseDto> response = new ArrayList<>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            response.add(new ExecuteResultResponseDto(entry.getKey(),entry.getValue()));
        }
        return response;
    }
    // 문제 채점 실행
    // TODO: 실행 시간, 메모리 사용량을 Problem Service에서 가져온 후 초과하지는 않는지 확인 하도록 한다.
    // TODO: 채점은 비동기로 진행되어야 한다.
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
        Map<String,String> result = executeCode(executeRequestDto, input,output,problemTimeLimit);
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
        // Submission 엔티티 저장
        Submission submission = Submission.builder()
                .memberId(memberId)
                .problemId(problemId)
                .submissionCode(executeRequestDto.getCode())
                .submissionStatus(isCorrect ? Status.CORRECT : Status.INCORRECT)
                .submissionLang(Language.fromLabel(executeRequestDto.getLanguage()))
                .build();
        // Member 엔티티에 memberRank 반영
        memberServiceFeignClient.updateMemberRank(memberId,memberRank+problemScore);
        // 반환
        return new SubmissionResultResponseDto(isCorrect,memberRank,problemScore,memberRank+problemScore,ratingChanged,rating,afterRating);
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
                if (timeElapsed > timeLimit * 1000000) {
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
}
