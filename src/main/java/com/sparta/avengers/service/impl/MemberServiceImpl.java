package com.sparta.avengers.service.impl;


import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.MemberRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;
import com.sparta.avengers.entity.Member;
import com.sparta.avengers.jwt.dto.TokenDto;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.entity.RefreshToken;
import com.sparta.avengers.repository.MemberRepository;
import com.sparta.avengers.repository.RefreshTokenRepository;
import com.sparta.avengers.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public GlobalResDto signup(MemberRequestDto memberRequestDto) {
        // userId 중복 검사
        if (memberRepository.findByName(memberRequestDto.getName()).isPresent()) {
            throw new RuntimeException("SignUp Fail Cause Overlap");
        }

        memberRequestDto.setPassword(passwordEncoder.encode(memberRequestDto.getPassword()));
        com.sparta.avengers.entity.Member member = new com.sparta.avengers.entity.Member(memberRequestDto);
        memberRepository.save(member);

        return new GlobalResDto("Signup Success", HttpStatus.OK.value());
    }

    @Override
    @Transactional
    public GlobalResDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        Member member = memberRepository.findByName(loginRequestDto.getName()).orElseThrow(
                ()->new RuntimeException("Not Find Member")
        );

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())){
            throw new RuntimeException("Not Match Password");
        }

        // 토큰 발급
        TokenDto tokenDto = jwtUtil.createAllToken(member.getName());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberUserId(member.getName());

        if(refreshToken.isPresent()) {
            refreshToken.get().setRefreshToken(tokenDto.getRefreshToken());
            refreshTokenRepository.save(refreshToken.get());
        }else{
            RefreshToken newRefreshToken = new RefreshToken(tokenDto.getRefreshToken(), member.getName());
            refreshTokenRepository.save(newRefreshToken);
        }

        setTokenOnHeader(response, tokenDto);

        return new GlobalResDto("Login Success", HttpStatus.OK.value());
    }

    private void setTokenOnHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }


}