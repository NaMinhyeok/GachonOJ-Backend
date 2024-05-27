package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteRequestDto;
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
public class ExecuteService {
    // 코드 실행하는 메소드
    public Map<String,String> executeCode(ExecuteRequestDto executeRequestDto, List<String> inputList, List<String> outputList, Integer timeLimit) {
        try {
            Map<String,String> result = new HashMap<>();
            // /home/exec 디렉토리 생성
            Path execDir = Paths.get("/home/exec");
            Files.createDirectories(execDir);
            log.info("Directory created");

            // /home/exec 디렉토리에 Main.java 파일 저장
            Path filePath = switch (executeRequestDto.getLanguage()) {
                case "Java" -> Paths.get(execDir.toString(), "Main.java");
                case "C++" -> Paths.get(execDir.toString(), "Main.cpp");
                case "C" -> Paths.get(execDir.toString(), "Main.c");
                case "Python" -> Paths.get(execDir.toString(), "Main.py");
                case "JavaScript" -> Paths.get(execDir.toString(), "Main.js");
                default ->
                        throw new IllegalArgumentException("Unsupported language: " + executeRequestDto.getLanguage());
            };
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
                runProcess.onExit().thenAccept(p -> {
                    log.info("Child process exited with exit code " + p.exitValue());
                });
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
