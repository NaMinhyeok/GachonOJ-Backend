package com.gachonoj.memberservice.service;

import com.gachonoj.memberservice.common.codes.ErrorCode;
import com.gachonoj.memberservice.domain.constant.Role;
import com.gachonoj.memberservice.domain.dto.request.LoginRequestDto;
import com.gachonoj.memberservice.domain.dto.request.MemberLangRequestDto;
import com.gachonoj.memberservice.domain.dto.request.SignUpRequestDto;
import com.gachonoj.memberservice.domain.dto.response.*;
import com.gachonoj.memberservice.domain.entity.Member;
//import com.gachonoj.memberservice.jwt.JwtTokenProvider;
import com.gachonoj.memberservice.repository.MemberRepository;
import io.jsonwebtoken.Jwt;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubmissionServiceFeignClient submissionServiceFeignClient;
    private final ProblemServiceFeignClient problemServiceFeignClient;

    private static final int PAGE_SIZE = 10;

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
    public void verifyEmail(String email, String inputAuthCode) {
        String key = Integer.toString(authCode);
        String value = redisService.getData(key);
        if(value != null && value.equals(email) && inputAuthCode.equals(key)) {
            redisService.deleteData(key);
        } else {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
    }
    // 이메일 인증코드를 전송하는 메소드
    public String joinEmail(String email) {
        // 이메일 유효성 검사
        isValidEmail(email);

        //유효성 검사가 통과하면 이메일 인증코드 생성 및 전송
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
    // 이메일 인증 코드 전송
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
        redisService.setDataExpire(toMail,Integer.toString(authCode), 300L);
    }
    //TODO : 캡슐화
    // 비밀번호 암호화 엔티티객체에 구현하여 캡슐화하기

    // 회원가입
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 회원가입 유효성 검사
        verifySignUp(signUpRequestDto);

        // 회원가입 로직
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member member = Member.builder()
                .memberEmail(signUpRequestDto.getMemberEmail())
                .memberName(signUpRequestDto.getMemberName())
                .memberNumber(signUpRequestDto.getMemberNumber())
                .memberPassword(passwordEncoder.encode(signUpRequestDto.getMemberPassword()))
                .memberNickname(signUpRequestDto.getMemberNickname())
                .build();

        memberRepository.save(member);
    }
    // 회원가입 유효성 검사
    public void verifySignUp(SignUpRequestDto signUpRequestDto) {
        String regExp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^])(?=.*[0-9]).{8,25}$";
        if(!signUpRequestDto.getMemberPassword().matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함한 8~25자여야 합니다.");
        }
        if(!signUpRequestDto.getMemberPassword().equals(signUpRequestDto.getMemberPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if(validateMemberNumber(signUpRequestDto.getMemberNumber())) {
            throw new IllegalArgumentException("이미 가입된 학번입니다.");
        }
        if(validateMemberEmail(signUpRequestDto.getMemberEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }
    // 이메일 유효성 검사 (현재 가입된 이메일인지 확인)
    public void isValidEmail(String email) {
        if(validateMemberEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if(!email.endsWith("@gachon.ac.kr")) {
            throw new IllegalArgumentException("가천대학교 이메일이 아닙니다.");
        }
    }

    // 학번 중복 검사
    public boolean validateMemberNumber(String memberNumber) {
        return memberRepository.existsByMemberNumber(memberNumber);
    }
    // 이메일 중복 검사
    public boolean validateMemberEmail(String memberEmail) {
        return memberRepository.existsByMemberEmail(memberEmail);
    }
    // 비밀번호 일치 검사
    public boolean validateMemberPassword(String memberPassword, Member member){
        return passwordEncoder.matches(memberPassword, member.getMemberPassword());
    }
    // 회원 정보 가져오기
    public Member loadUserByUsername(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 회원입니다."));
    }
    // 닉네임 중복 확인
    public NicknameVerificationResponseDto verifyMemberNickname(String memberNickname) {
        return new NicknameVerificationResponseDto(memberRepository.existsByMemberNickname(memberNickname));
    }
    // 호버시 회원 정보 조회
    public HoverResponseDto getHoverInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(memberId);
        return new HoverResponseDto(member.getMemberEmail(), member.getMemberNickname(), rating);
    }
    // 사용자 정보 조회 ( 사용자 정보 수정 화면에서 정보 조회)
    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(memberId);
        return new MemberInfoResponseDto(member.getMemberEmail(), member.getMemberName(), member.getMemberNumber(), member.getMemberIntroduce(), member.getMemberNickname(), member.getMemberImg(), rating);
    }
    // 사용자 본인 정보 수정
    // 사용자 선호 언어 조회
    public MemberLangResponseDto getMemberLang(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        return new MemberLangResponseDto(member.getMemberLang());
    }
    // 사용자 선호 언어 수정
    @Transactional
    public void updateMemberLang(Long memberId, MemberLangRequestDto memberLang) {
        Member member = memberRepository.findByMemberId(memberId);
        member.updateMemberLang(memberLang);
    }
    // 대회 페이지 사용자 정보 조회
    public MemberInfoExamResponseDto getMemberInfoExam(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(memberId);
        return new MemberInfoExamResponseDto(member.getMemberNickname(), rating, member.getMemberName(), member.getMemberNumber());
    }
    // rating 계산
    public Integer calculateRating(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        if(member.getMemberRank() < 1000) {
            return 0;
        } else if(member.getMemberRank() < 1200) {
            return 1;
        } else if(member.getMemberRank() < 1400) {
            return 2;
        } else if(member.getMemberRank() < 1600) {
            return 3;
        } else if(member.getMemberRank() < 1900) {
            return 4;
        } else if(member.getMemberRank() < 2200) {
            return 5;
        } else if(member.getMemberRank() < 2500) {
            return 6;
        } else {
            return 7;
        }
    }
    // 랭킹 화면 사용자 정보 조회
    @Transactional
    public MemberInfoRankingResponseDto getMemberInfoRanking(Long memberId) {
        try {
            SubmissionMemberInfoResponseDto submissionMemberInfoResponseDto = submissionServiceFeignClient.getMemberInfoBySubmission(memberId);
            Integer bookmarkProblemCount = problemServiceFeignClient.getBookmarkCountByMemberId(memberId);
            Member member = memberRepository.findByMemberId(memberId);
            Integer rating = calculateRating(memberId);
            return new MemberInfoRankingResponseDto(member.getMemberNickname(),rating,submissionMemberInfoResponseDto.getSolvedProblemCount(), submissionMemberInfoResponseDto.getTryProblemCount(),bookmarkProblemCount);
        } catch (Exception e) {
            log.error(ErrorCode.OTHER_SERVICE_CONNECTION_FAILURE.getMessage());
            throw new IllegalArgumentException(ErrorCode.OTHER_SERVICE_CONNECTION_FAILURE.getMessage());
        }
    }
    // 사용자 목록 조회
    public Page<MemberListResponseDto> getMemberList(String memberRole, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "memberId"));
        if(Objects.equals(memberRole, "학생")) {
            Page<Member> memberList = memberRepository.findByMemberRole(Role.ROLE_STUDENT, pageable);
            return memberList.map(MemberListResponseDto::new);
        } else if(Objects.equals(memberRole, "교수")) {
            Page<Member> memberList = memberRepository.findByMemberRole(Role.ROLE_PROFESSOR, pageable);
            return memberList.map(MemberListResponseDto::new);
        } else if(Objects.equals(memberRole, "관리자")) {
            Page<Member> memberList = memberRepository.findByMemberRole(Role.ROLE_ADMIN, pageable);
            return memberList.map(MemberListResponseDto::new);
        } else {
            throw new IllegalArgumentException(ErrorCode.NOT_VALID_ERROR.getMessage());
        }
    }
}
