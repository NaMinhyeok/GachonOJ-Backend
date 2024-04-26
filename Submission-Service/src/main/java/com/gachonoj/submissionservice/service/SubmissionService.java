package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.domain.dto.response.ExecuteRsultResponseDto;
import com.gachonoj.submissionservice.fegin.client.ProblemServiceFeignClient;
import com.gachonoj.submissionservice.fegin.dto.response.SubmissionProblemTestCaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProblemServiceFeignClient problemServiceFeignClient;

    // 문제 채점 실행
    @Transactional
    public List<ExecuteRsultResponseDto> executeCodeByProblemId(ExecuteTestRequestDto executeTestRequestDto,Long problemId) {
        List<String> input = problemServiceFeignClient.getTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getInput)
                .toList();
        List<String> output = problemServiceFeignClient.getTestCases(problemId).stream()
                .map(SubmissionProblemTestCaseResponseDto::getOutput)
                .toList();
        List<String> result = executeCode(executeTestRequestDto, input,output);
        List<ExecuteRsultResponseDto> response = new ArrayList<>();
        for (String s : result) {
            response.add(new ExecuteRsultResponseDto(s));
        }
        return response;
    }
    // 코드 실행
    @Transactional
    public List<String> executeCode(ExecuteTestRequestDto executeTestRequestDto, List<String> inputList,List<String> outputList) {
        try {
            List<String> result = new ArrayList<>();
            // /home/exec 디렉토리 생성
            Path execDir = Paths.get("/home/exec");
            Files.createDirectories(execDir);
            log.info("Directory created");

            // /home/exec 디렉토리에 Main.java 파일 저장
            Path filePath = Paths.get(execDir.toString(), "Main." + executeTestRequestDto.getLanguage());
            Files.write(filePath, executeTestRequestDto.getCode().getBytes());
            log.info("Code saved");

            // 컴파일
            ProcessBuilder compileProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
                case "java" -> new ProcessBuilder("javac", "Main.java");
                case "cpp" -> new ProcessBuilder("g++", "-o", "Main", "Main.cpp");
                case "c" -> new ProcessBuilder("gcc", "-o", "Main", "Main.c");
                case "py", "js" -> // Python,JavaScript는 컴파일이 필요 없습니다.
                        null;
                default ->
                        throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
            };
            if (compileProcessBuilder != null) {
                compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
                Process compileProcess = compileProcessBuilder.start();
                compileProcess.waitFor();
                log.info("Code compiled");
            }
            for(int i=0; i<inputList.size(); i++){
                // 실행
                ProcessBuilder runProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
                    case "java" -> new ProcessBuilder("java", "Main");
                    case "cpp", "c" -> new ProcessBuilder("./Main");
                    case "py" -> new ProcessBuilder("python3", "Main.py");
                    case "js" -> new ProcessBuilder("node", "Main.js");
                    default ->
                            throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
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
                    result.add(errorResult.toString());
                    break;
                }

                long endTime = System.nanoTime(); // 종료 시간
                long timeElapsed = endTime - startTime; // 소요시간
                log.info("Execution time in nanoseconds: " + timeElapsed);
                log.info("Execution time in milliseconds: " + timeElapsed / 1000000);
                log.info("Code output: " + i + "번째" + outputResult.toString());
                // 정답인지 확인
                if(outputList.get(i).equals(outputResult.toString())){
                    result.add("정답");
                }else{
                    result.add("오답");
                }
                result.add(outputResult.toString());
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
            List<String> result = new ArrayList<>();
            result.add("Error: " + e.getMessage());
            return result;
        }
    }
//    // 코드 실행
//    public String executeCode(String code, String language, List<String> input,List<String> output) {
//        try {
//            // /home/exec 디렉토리 생성
//            Path execDir = Paths.get("/home/exec");
//            Files.createDirectories(execDir);
//            log.info("Directory created");
//
//            // /home/exec 디렉토리에 Main.java 파일 저장
//            Path filePath = Paths.get(execDir.toString(), "Main." + language);
//            Files.write(filePath, code.getBytes());
//            log.info("Code saved");
//
//            // 컴파일
//            ProcessBuilder compileProcessBuilder = switch (language) {
//                case "java" -> new ProcessBuilder("javac", "Main.java");
//                case "cpp" -> new ProcessBuilder("g++", "-o", "Main", "Main.cpp");
//                case "c" -> new ProcessBuilder("gcc", "-o", "Main", "Main.c");
//                case "py", "js" -> // Python,JavaScript는 컴파일이 필요 없습니다.
//                        null;
//                default ->
//                        throw new IllegalArgumentException("Unsupported language: " + language);
//            };
//            if (compileProcessBuilder != null) {
//                compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
//                Process compileProcess = compileProcessBuilder.start();
//                compileProcess.waitFor();
//                log.info("Code compiled");
//            }
//
//            // 실행
//            ProcessBuilder runProcessBuilder = switch (language) {
//                case "java" -> new ProcessBuilder("java", "Main");
//                case "cpp", "c" -> new ProcessBuilder("./Main");
//                case "py" -> new ProcessBuilder("python3", "Main.py");
//                case "js" -> new ProcessBuilder("node", "Main.js");
//                default ->
//                        throw new IllegalArgumentException("Unsupported language: " + language);
//            };
//            runProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
//            Process runProcess = runProcessBuilder.start();
//            log.info("Code executed");
//
//            // 입력 제공
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
//            writer.write(B18870);
//            writer.flush();
//            writer.close();
//
//            // 결과 가져오기
//            BufferedReader stdInput = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
//            BufferedReader stdError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
//
//            StringBuilder outputResult = new StringBuilder();
//            String line;
//            while ((line = stdInput.readLine()) != null) {
//                outputResult.append(line).append("\n");
//            }
//            while ((line = stdError.readLine()) != null) {
//                outputResult.append(line).append("\n");
//            }
//            log.info("Code output: " + outputResult.toString());
//
//            // /home/exec 디렉토리 삭제
//            Files.walk(execDir)
//                    .sorted(Comparator.reverseOrder())
//                    .map(Path::toFile)
//                    .forEach(File::delete);
//            log.info("Directory deleted");
//
//            return output.toString();
//        } catch (Exception e) {
//            log.info("Error: " + e.getMessage());
//            return "Error: " + e.getMessage();
//        }
//    }
}
