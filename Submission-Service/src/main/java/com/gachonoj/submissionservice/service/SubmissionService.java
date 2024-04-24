package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final String B18870 = "5\n" +
            "2 4 -10 4 -9";
    // 코드 실행
//    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
//        try {
//            // /home/exec 디렉토리 생성
//            Path execDir = Paths.get("/home/exec");
//            Files.createDirectories(execDir);
//            log.info("Directory created");
//
//            // /home/exec 디렉토리에 Main.java 파일 저장
//            Path filePath = Paths.get(execDir.toString(), "Main." + executeTestRequestDto.getLanguage());
//            Files.write(filePath, executeTestRequestDto.getCode().getBytes());
//            log.info("Code saved");
//
//            // 컴파일
//            ProcessBuilder compileProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
//                case "java" -> new ProcessBuilder("javac", "Main.java");
//                case "cpp" -> new ProcessBuilder("g++", "-o", "Main", "Main.cpp");
//                case "c" -> new ProcessBuilder("gcc", "-o", "Main", "Main.c");
//                case "py", "js" -> // Python,JavaScript는 컴파일이 필요 없습니다.
//                        null;
//                default ->
//                        throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
//            };
//            if (compileProcessBuilder != null) {
//                compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
//                Process compileProcess = compileProcessBuilder.start();
//                compileProcess.waitFor();
//                log.info("Code compiled");
//            }
//
//            // 실행
//            ProcessBuilder runProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
//                case "java" -> new ProcessBuilder("java", "Main");
//                case "cpp", "c" -> new ProcessBuilder("./Main");
//                case "py" -> new ProcessBuilder("python3", "Main.py");
//                case "js" -> new ProcessBuilder("node", "Main.js");
//                default ->
//                        throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
//            };
//            runProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
//            Process runProcess = runProcessBuilder.start();
//            log.info("Code executed");
//
//            // 입력 제공
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
//            writer.write(input);
//            writer.flush();
//            writer.close();
//
//            // 결과 가져오기
//            BufferedReader stdInput = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
//            BufferedReader stdError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
//
//            StringBuilder output = new StringBuilder();
//            String line;
//            while ((line = stdInput.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//            while ((line = stdError.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//            log.info("Code output: " + output.toString());
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
    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
        try {
            Path execDir = createExecutionDirectory();
            saveCodeToFile(executeTestRequestDto, execDir);
            compileCode(executeTestRequestDto, execDir);
            Process runProcess = executeCode(executeTestRequestDto, execDir);
            provideInput(runProcess, B18870);
            String output = getOutput(runProcess);
            deleteExecutionDirectory(execDir);
            return output;
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    private Path createExecutionDirectory() throws IOException {
        Path execDir = Paths.get("/home/exec");
        Files.createDirectories(execDir);
        log.info("Directory created");
        return execDir;
    }

    private void saveCodeToFile(ExecuteTestRequestDto executeTestRequestDto, Path execDir) throws IOException {
        Path filePath = Paths.get(execDir.toString(), "Main." + executeTestRequestDto.getLanguage());
        Files.write(filePath, executeTestRequestDto.getCode().getBytes());
        log.info("Code saved");
    }

    private void compileCode(ExecuteTestRequestDto executeTestRequestDto, Path execDir) throws IOException, InterruptedException {
        ProcessBuilder compileProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
            case "java" -> new ProcessBuilder("javac", "Main.java");
            case "cpp" -> new ProcessBuilder("g++", "-o", "Main", "Main.cpp");
            case "c" -> new ProcessBuilder("gcc", "-o", "Main", "Main.c");
            case "py", "js" -> null; // Python,JavaScript는 컴파일이 필요 없습니다.
            default -> throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
        };
        if (compileProcessBuilder != null) {
            compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();
            log.info("Code compiled");
        }
    }

    private Process executeCode(ExecuteTestRequestDto executeTestRequestDto, Path execDir) throws IOException {
        ProcessBuilder runProcessBuilder = switch (executeTestRequestDto.getLanguage()) {
            case "java" -> new ProcessBuilder("java", "Main");
            case "cpp", "c" -> new ProcessBuilder("./Main");
            case "py" -> new ProcessBuilder("python3", "Main.py");
            case "js" -> new ProcessBuilder("node", "Main.js");
            default -> throw new IllegalArgumentException("Unsupported language: " + executeTestRequestDto.getLanguage());
        };
        runProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
        Process runProcess = runProcessBuilder.start();
        log.info("Code executed");
        return runProcess;
    }

    private void provideInput(Process runProcess,String input) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
        writer.write(input);
        writer.flush();
        writer.close();
    }

    private String getOutput(Process runProcess) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = stdInput.readLine()) != null) {
            output.append(line).append("\n");
        }
        while ((line = stdError.readLine()) != null) {
            output.append(line).append("\n");
        }
        log.info("Code output: " + output.toString());
        return output.toString();
    }

    private void deleteExecutionDirectory(Path execDir) throws IOException {
        Files.walk(execDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        log.info("Directory deleted");
    }
}
