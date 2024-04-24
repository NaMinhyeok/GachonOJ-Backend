package com.gachonoj.submissionservice.service;

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
    public String executeCode(String code) {
        try {
            // 코드를 파일로 저장
            Files.write(Paths.get("/home/main.java"), code.getBytes());

            // 컴파일
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "main.java");
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // 실행
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "main");
            Process runProcess = runProcessBuilder.start();

            // 결과 가져오기
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

}
