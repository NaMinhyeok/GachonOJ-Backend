package com.gachonoj.memberservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisService redisService;
    // 이메일 인증 코드
    private int authCode;
    // 이메일 인증코드를 위한 난수 생성기
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authCode = Integer.parseInt(randomNumber);
    }
    // 이메일 인증코드를 확인하는 메소드

    // 이메일 인증코드를 전송하는 메소드
    public String joinEmail(String email) {
        makeRandomNumber();
        String setFrom = "gachonlastdance@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "GachonOJ 회원 가입 인증 이메일"; // 이메일 제목
        String content =
                "GachonOJ에 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 " + authCode + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요" +
                        "<br>" +
                        "인증번호는 5분 뒤 만료됩니다."; //이메일 내용 삽
        sendEmail(setFrom, toMail, title, content);
        return Integer.toString(authCode);
    }
    // 이메일 전송
    public void sendEmail(String setFrom, String toMail, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(setFrom); // 보내는사람 생략하면 정상작동을 안함
            messageHelper.setTo(toMail); // 받는사람 이메일
            messageHelper.setSubject(title); // 메일제목은 생략이 가능
            messageHelper.setText(content, true); // 메일 내용
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisService.setDataExpire(Integer.toString(authCode), toMail, 300L);
    }
}
