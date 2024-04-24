package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    // 코드 실행
    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
        try {
            // 코드를 파일로 저장
            Files.write(Paths.get("/home/main.java"), executeTestRequestDto.getCode().getBytes());
            log.info("Code saved");
            // 컴파일
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "main.java");
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();
            log.info("Code compiled");
            // 실행
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "main");
            Process runProcess = runProcessBuilder.start();
            log.info("Code executed");
            // 결과 가져오기
            // 결과 가져오기
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
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

}
