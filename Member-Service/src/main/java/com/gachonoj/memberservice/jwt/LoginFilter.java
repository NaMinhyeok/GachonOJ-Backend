package com.gachonoj.memberservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gachonoj.memberservice.common.codes.ErrorCode;
import com.gachonoj.memberservice.domain.dto.request.LoginRequestDto;
import com.gachonoj.memberservice.common.response.CommonResponseDto;
import com.gachonoj.memberservice.domain.dto.response.LoginResponseDto;
import com.gachonoj.memberservice.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    public LoginFilter(AuthenticationManager authenticationManager,JwtUtil jwtUtil,RedisService redisService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        setFilterProcessesUrl("/member/login");
    }
    // 로그인 시도 시 실행되는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청에서 LoginRequestDto 객체를 추출
            LoginRequestDto loginRequestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            // 유저 정보 검증을 위해 이메일, 패스워드 값 전달
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getMemberEmail(), loginRequestDto.getMemberPassword(), null);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 로그인 성공 시 실행되는 메소드 (여기서 JWT 토큰 생성)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        Long memberId = customUserDetails.getMemberId();
        String memberImg = customUserDetails.getMemberImg();
        String memberRole = customUserDetails.getMemberRole();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createAccessJwt(role,memberId);
        String refreshToken = jwtUtil.createRefreshJwt(role,memberId);

        redisService.setDataExpire(Long.toString(memberId),refreshToken,jwtUtil.getRefreshTokenExpireTime());

        LoginResponseDto loginResponseDto = new LoginResponseDto(memberImg,memberRole); // 여기에 필요한 데이터를 설정하세요.
        CommonResponseDto<LoginResponseDto> commonResponseDto = CommonResponseDto.success(loginResponseDto);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // LoginResponseDto를 JSON으로 변환
        String json = mapper.writeValueAsString(commonResponseDto);

        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", "Bearer" + refreshToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
    // 로그인 실패 시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CommonResponseDto<String> commonResponseDto = CommonResponseDto.fail(ErrorCode.FAIL_LOGIN, "로그인 실패");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = mapper.writeValueAsString(commonResponseDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
