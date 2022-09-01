package com.sparta.avengers.service.impl;


import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.AccountRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;
import com.sparta.avengers.jwt.dto.TokenDto;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.model.Account;
import com.sparta.avengers.model.RefreshToken;
import com.sparta.avengers.repository.AccountRepository;
import com.sparta.avengers.repository.RefreshTokenRepository;
import com.sparta.avengers.service.AccountService;
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
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public GlobalResDto signup(AccountRequestDto accountReqDto) {
        // userId 중복 검사
        if (accountRepository.findByUserId(accountReqDto.getUserId()).isPresent()) {
            throw new RuntimeException("SignUp Fail Cause Overlap");
        }

        accountReqDto.setPassword(passwordEncoder.encode(accountReqDto.getPassword()));
        Account account = new Account(accountReqDto);
        accountRepository.save(account);

        return new GlobalResDto("Signup Success", HttpStatus.OK.value());
    }

    @Override
    @Transactional
    public GlobalResDto login(LoginRequestDto loginReqDot, HttpServletResponse response) {

        Account account = accountRepository.findByUserId(loginReqDot.getUserId()).orElseThrow(
                ()->new RuntimeException("Not Find Account")
        );

        if(!passwordEncoder.matches(loginReqDot.getPassword(), account.getPassword())){
            throw new RuntimeException("Not Match Password");
        }

        // 토큰 발급
        TokenDto tokenDto = jwtUtil.createAllToken(account.getUserId());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountUserId(account.getUserId());

        if(refreshToken.isPresent()) {
            refreshToken.get().setRefreshToken(tokenDto.getRefreshToken());
            refreshTokenRepository.save(refreshToken.get());
        }else{
            RefreshToken newRefreshToken = new RefreshToken(tokenDto.getRefreshToken(), account.getUserId());
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