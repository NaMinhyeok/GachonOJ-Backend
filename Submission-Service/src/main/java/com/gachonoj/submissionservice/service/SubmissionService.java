package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    // 코드 실행
//    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
//        try {
//            // 코드를 파일로 저장
//            Files.write(Paths.get("/home/Main.java"), executeTestRequestDto.getCode().getBytes());
//            log.info("Code saved");
//
//            // 컴파일
//            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "Main.java");
//            compileProcessBuilder.directory(new File("/home")); // 작업 디렉토리 설정
//            Process compileProcess = compileProcessBuilder.start();
//            compileProcess.waitFor();
//            log.info("Code compiled");
//
//            // 실행
//            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "Main");
//            runProcessBuilder.directory(new File("/home")); // 작업 디렉토리 설정
//            Process runProcess = runProcessBuilder.start();
//            log.info("Code executed");
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
//            return output.toString();
//        } catch (Exception e) {
//            log.info("Error: " + e.getMessage());
//            return "Error: " + e.getMessage();
//        }
//    }
    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
        Process process = null;
        Path dirPath = Paths.get("/home/temp");
        Path javaFilePath = dirPath.resolve("Main.java");
        try {
            // 디렉토리 생성
            Files.createDirectory(dirPath);
            log.info("Directory created");

            // 코드를 파일로 저장
            Files.write(javaFilePath, executeTestRequestDto.getCode().getBytes());
            log.info("Code saved");

            // 컴파일
            int compileExit = new ProcessExecutor().command("javac", javaFilePath.toString())
                    .redirectOutput(new LogOutputStream() {
                        @Override
                        protected void processLine(String line) {
                            log.info(line);
                        }
                    }).execute().getExitValue();
            log.info("Code compiled with exit code " + compileExit);

            // 실행
            process = new ProcessExecutor().command("java", "-cp", dirPath.toString(), "Main")
                    .start().getProcess();
            log.info("Code executed");

            // 결과 가져오기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            log.info("Code output: " + output.toString());

            return output.toString();
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        } finally {
            // 프로세스 종료
            if (process != null) {
                process.destroy();
                log.info("Process destroyed");
            }

            // 디렉토리 삭제
            try {
                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                log.info("Directory deleted");
            } catch (Exception e) {
                log.info("Failed to delete directory: " + e.getMessage());
            }
        }
    }

}
