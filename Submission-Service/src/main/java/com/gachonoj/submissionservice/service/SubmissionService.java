package com.gachonoj.submissionservice.service;

import com.gachonoj.submissionservice.domain.dto.request.ExecuteTestRequestDto;
import com.gachonoj.submissionservice.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {
    private static final String input = "5\n" +
                                        "2 4 -10 4 -9";
    // 코드 실행
    public String executeCode(ExecuteTestRequestDto executeTestRequestDto) {
        try {
            // /home/exec 디렉토리 생성
            Path execDir = Paths.get("/home/exec");
            Files.createDirectories(execDir);
            log.info("Directory created");

            // /home/exec 디렉토리에 Main.java 파일 저장
            Path filePath = Paths.get(execDir.toString(), "Main.java");
            Files.write(filePath, executeTestRequestDto.getCode().getBytes());
            log.info("Code saved");

            // 컴파일
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "Main.java");
            compileProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();
            log.info("Code compiled");

            // 실행
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "Main");
            runProcessBuilder.directory(new File(execDir.toString())); // 작업 디렉토리 설정
            Process runProcess = runProcessBuilder.start();
            log.info("Code executed");

            // 입력 제공
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
            writer.write(input);
            writer.flush();
            writer.close();

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

            // /home/exec 디렉토리 삭제
            Files.walk(execDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            log.info("Directory deleted");

            return output.toString();
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
