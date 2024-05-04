package com.gachonoj.memberservice.service;

import com.gachonoj.memberservice.common.codes.ErrorCode;
import com.gachonoj.memberservice.domain.constant.Role;
import com.gachonoj.memberservice.domain.dto.request.*;
import com.gachonoj.memberservice.domain.dto.response.*;
import com.gachonoj.memberservice.domain.entity.Member;
//import com.gachonoj.memberservice.jwt.JwtTokenProvider;
import com.gachonoj.memberservice.feign.client.ProblemServiceFeignClient;
import com.gachonoj.memberservice.feign.client.SubmissionServiceFeignClient;
import com.gachonoj.memberservice.feign.dto.response.SubmissionMemberInfoResponseDto;
import com.gachonoj.memberservice.repository.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

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
    private final S3UploadService s3UploadService;

    private static final int PAGE_SIZE = 10;

    // 이메일 인증코드를 위한 난수 생성기
    public int makeRandomNumber() {
        Random r = new Random();
        int authCode;
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authCode = Integer.parseInt(randomNumber);
        return authCode;
    }
    // 이메일 인증코드를 확인하는 메소드
    public void verifyEmail(String email, String inputAuthCode) {
        String value = redisService.getData(email);
        if(value != null && value.equals(inputAuthCode)) {
            redisService.deleteData(email);
        } else if(value == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }
        else {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
    }
    // 이메일 인증코드를 전송하는 메소드
    public String joinEmail(String email) {
        // 이메일 유효성 검사
        isValidEmail(email);

        //유효성 검사가 통과하면 이메일 인증코드 생성 및 전송
        int authCode = makeRandomNumber();
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
        redisService.setDataExpire(toMail,Integer.toString(authCode), 300L);
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
    }
    //TODO : 캡슐화
    // 비밀번호 암호화 엔티티객체에 구현하여 캡슐화하기

    // 회원가입
    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        // 회원가입 유효성 검사
        verifySignUp(signUpRequestDto.getMemberPassword(), signUpRequestDto.getMemberPasswordConfirm(), signUpRequestDto.getMemberEmail(), signUpRequestDto.getMemberNumber());

        // 회원가입 로직
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member member = Member.builder()
                .memberEmail(signUpRequestDto.getMemberEmail())
                .memberName(signUpRequestDto.getMemberName())
                .memberNumber(signUpRequestDto.getMemberNumber())
                .memberPassword(passwordEncoder.encode(signUpRequestDto.getMemberPassword()))
                .memberNickname(signUpRequestDto.getMemberNickname())
                .memberRole(Role.ROLE_STUDENT)
                .build();

        memberRepository.save(member);
    }
    // 회원가입 유효성 검사
    public void verifySignUp(String password, String passwordConfirm, String email, String number) {
        String regExp = "^(?=.*[a-zA-Z])(?=.*[~!@#$%&*()_+=?])(?=.*[0-9]).{8,25}$";
        if(!password.matches(regExp)) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함한 8~25자여야 합니다.");
        }
        if(!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if(number!=null){
            if(validateMemberNumber(number)) {
                throw new IllegalArgumentException("이미 가입된 학번입니다.");
            }
        }
        if(validateMemberEmail(email)) {
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
    public NicknameVerificationResponseDto verifiedMemberNickname(MemberNicknameRequestDto memberNicknameRequestDto) {
        String memberNickname = memberNicknameRequestDto.getMemberNickname();
        return new NicknameVerificationResponseDto(verifyMemberNickname(memberNickname));
    }
    // 호버시 회원 정보 조회
    public HoverResponseDto getHoverInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(member.getMemberRank());
        return new HoverResponseDto(member.getMemberEmail(), member.getMemberNickname(), rating);
    }
    // 사용자 정보 조회 ( 사용자 정보 수정 화면에서 정보 조회)
    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(member.getMemberRank());
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
        Integer rating = calculateRating(member.getMemberRank());
        return new MemberInfoExamResponseDto(member.getMemberNickname(), rating, member.getMemberName(), member.getMemberNumber());
    }
    // rating 계산
    public Integer calculateRating(Integer rank) {
        if(rank < 1000) {
            return 0;
        } else if(rank < 1200) {
            return 1;
        } else if(rank < 1400) {
            return 2;
        } else if(rank < 1600) {
            return 3;
        } else if(rank < 1900) {
            return 4;
        } else if(rank < 2200) {
            return 5;
        } else if(rank < 2500) {
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
            Integer rating = calculateRating(member.getMemberRank());
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
    // 사용자 랭킹 목록 조회
    //TODO
    // 한번에 검색이 잘 진행이 안되고있음 왜그런지 확인해서 추후에 수정하기
    @Transactional
    public Page<MemberRankingResponseDto> getMemberRankingList(int pageNo,String search) {
        Pageable pageable = PageRequest.of(pageNo-1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "memberRank"));
        Page<Member> memberList;
        if(search != null && !search.isEmpty()) {
            //
            memberList = memberRepository.findByMemberNicknameContainingAndMemberRole(search,Role.ROLE_STUDENT, pageable);
        } else {
            // 랭킹에 학생만 표시
            memberList = memberRepository.findByMemberRole(Role.ROLE_STUDENT, pageable);
        }
        return memberList.map(member -> {
            Integer memberSolved = submissionServiceFeignClient.getMemberSolved(member.getMemberId());
            return new MemberRankingResponseDto(member, memberSolved);
        });
    }
    // 회원정보 수정
    // TODO : JAXB is unavailable. Will fallback to SDK implementation which may be less performant.If you are using Java 9+, you will need to include javax.xml.bind:jaxb-api as a dependency. Waring 해결하기
    @Transactional
    public void updateMemberInfo(Long memberId, MultipartFile memberImg, MemberInfoRequestDto memberInfoRequestDto) throws IOException {
        Member member = memberRepository.findByMemberId(memberId);
        if(memberImg != null) {
            // 원래 있던 이미지 삭제
            if(member.getMemberImg() != null) {
                log.info("memberImg : " + member.getMemberImg());
                String imgUrl = member.getMemberImg();
                URL url = new URL(imgUrl);
                String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
                log.info("fileName : " + fileName);
                s3UploadService.deleteImage(fileName);
            }
            // 이미지 저장
            String memberImgUrl = s3UploadService.saveFile(memberImg);
            member.updateMemberInfo(memberInfoRequestDto.getMemberNickname(), memberInfoRequestDto.getMemberName(), memberInfoRequestDto.getMemberIntroduce(), memberImgUrl);
        } else {
            member.updateMemberInfo(memberInfoRequestDto.getMemberNickname(), memberInfoRequestDto.getMemberName(), memberInfoRequestDto.getMemberIntroduce(), member.getMemberImg());
        }
    }
    // 닉네임 중복 확인 True False 반환 메소드
    public boolean verifyMemberNickname(String memberNickname) {
        return !memberRepository.existsByMemberNickname(memberNickname);
    }
    // 회원 탈퇴
    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
    // 비밀번호 변경
    @Transactional
    public void updateMemberPassword(Long memberId, UpdatePasswordRequestDto updatePasswordRequestDto) {
        Member member = memberRepository.findByMemberId(memberId);
        if(!validateMemberPassword(updatePasswordRequestDto.getMemberPassword(), member)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        } else if(!updatePasswordRequestDto.getMemberNewPassword().equals(updatePasswordRequestDto.getMemberNewPasswordConfirm())) {
            throw new IllegalArgumentException("새로운 비밀번호가 일치하지 않습니다.");
        } else {
            member.updateMemberPassword(passwordEncoder.encode(updatePasswordRequestDto.getMemberNewPassword()));
        }
    }
    // 사용자 추가 생성
    @Transactional
    public void createMember(CreateMemberRequestDto createMemberRequestDto) {
        // 회원가입 유효성 검사
        verifySignUp(createMemberRequestDto.getMemberPassword(), createMemberRequestDto.getMemberPasswordConfirm(), createMemberRequestDto.getMemberEmail(), createMemberRequestDto.getMemberNumber());
        Role memberRole = Role.fromLabel(createMemberRequestDto.getMemberRole());

        // 회원가입 로직
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member member = Member.builder()
                .memberEmail(createMemberRequestDto.getMemberEmail())
                .memberName(createMemberRequestDto.getMemberName())
                .memberNumber(createMemberRequestDto.getMemberNumber())
                .memberPassword(passwordEncoder.encode(createMemberRequestDto.getMemberPassword()))
                .memberNickname(createMemberRequestDto.getMemberNickname())
                .memberRole(memberRole)
                .build();

        memberRepository.save(member);
    }
    // 관리자가 사용자 정보 변경
    @Transactional
    public void updateMemberByAdmin(UpdateMemberRequestDto updateMemberRequestDto,Long memberId){
        Member member = memberRepository.findByMemberId(memberId);
        Role role = Role.fromLabel(updateMemberRequestDto.getMemberRole());
        member.updateMemberInfo(updateMemberRequestDto.getMemberName(),updateMemberRequestDto.getMemberNickname(),updateMemberRequestDto.getMemberNumber(),role);
    }
    // 관리자 화면 사용자 정보변경을 위한 사용자 정보 조회
    public MemberInfoByAdminResponseDto getMemberInfoByAdmin(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        return new MemberInfoByAdminResponseDto(member.getMemberEmail(),member.getMemberName(),member.getMemberNumber(),member.getMemberNickname(),member.getMemberRole().getLabel());
    }
    // 관리자가 회원 탈퇴 시키기
    @Transactional
    public void deleteMemberByAdmin(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    // 응시자 추가를 위한 사용자 정보 조회
    public List<MemberInfoTestResponseDto> getMemberInfoTest(String memberEmail, String memberNumber) {
        List<Member> members;
        if(memberEmail != null) {
            members = memberRepository.findByMemberEmailContaining(memberEmail);
        } else if(memberNumber != null) {
            members = memberRepository.findByMemberNumberContaining(memberNumber);
        } else {
            throw new IllegalArgumentException("이메일 또는 학번을 입력해주세요.");
        }
        return members.stream()
                .map(member -> new MemberInfoTestResponseDto(member.getMemberId(), member.getMemberImg(), member.getMemberName(), member.getMemberNumber(), member.getMemberEmail()))
                .collect(Collectors.toList());
    }
    // 로그아웃
    @Transactional
    public void logout(Long memberId) {
        redisService.deleteData(memberId.toString());
    }
    // 레이팅을 올리기 위한 필요한 점수 계산
    public Integer calculateNeedRating(Integer memberRank) {
        if(memberRank < 1000) {
            return 1000;
        } else if(memberRank < 1200) {
            return 1200-memberRank;
        } else if(memberRank < 1400) {
            return 1400-memberRank;
        } else if(memberRank < 1600) {
            return 1600-memberRank;
        } else if(memberRank < 1900) {
            return 1900-memberRank;
        } else if(memberRank < 2200) {
            return 2200-memberRank;
        } else if(memberRank < 2500) {
            return 2500-memberRank;
        } else {
            return 3000-memberRank;
        }
    }
    // 회원 정보 조회 문제 화면에서
    public MemberInfoProblemResponseDto getMemberInfoProblem(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        Integer rating = calculateRating(member.getMemberRank());
        Integer needRating = calculateNeedRating(member.getMemberRank());
        return new MemberInfoProblemResponseDto(member.getMemberNickname(), member.getMemberIntroduce(), member.getMemberImg(),rating,member.getMemberRank(),needRating);
    }
    // 비밀번호 찾기 이메일 전송
    @Transactional
    public void sendPasswordEmail(String memberEmail) {
        Member member = memberRepository.findByMemberEmail(memberEmail);
        if (member == null) {
            throw new IllegalArgumentException("가입되지 않은 이메일입니다.");
        }
        String newPassword = makeRandomPassword();
        member.updateMemberPassword(passwordEncoder.encode(newPassword));

        String setFrom = "gachonlastdance@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = memberEmail;
        String title = "GachonOJ 임시 비밀번호 이메일"; // 이메일 제목
        String content =
                "GachonOJ의 방문해주셔서 감사합니다." +    //html 형식으로 작성 !
                        "<br><br>" +
                        "회원님의 임시비밀번호는 " + newPassword + "입니다." +
                        "<br>"; //이메일 내용 삽
        sendEmail(setFrom, toMail, title, content);
    }
    // 비밀번호 찾기 난수 생성
    public String makeRandomPassword() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String OTHER_CHAR = "~!@#$%&*()_+-=?";
        String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;

        SecureRandom random = new SecureRandom();

        int passwordLength = random.nextInt(14)+12;

        StringBuilder password = new StringBuilder();
        for(int i=0; i<passwordLength; i++){
            int index = random.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(index));
        }
        return password.toString();
    }
}
