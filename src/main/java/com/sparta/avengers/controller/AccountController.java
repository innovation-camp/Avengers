package com.sparta.avengers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.AccountRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.security.user.UserDetailsImpl;
import com.sparta.avengers.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @PostMapping("/account/signup")
    public GlobalResDto signup(@RequestBody @Valid AccountRequestDto accountReqDto) {
        return accountService.signup(accountReqDto);
    }

    @PostMapping("/account/login")
    public GlobalResDto login(@RequestBody @Valid LoginRequestDto loginReqDot, HttpServletResponse response) {
        return accountService.login(loginReqDot, response);
    }

    @GetMapping("/issue/token")
    public GlobalResDto issuedToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, jwtUtil.createToken(userDetails.getAccount().getUserId(), "Access"));
        return new GlobalResDto("issuedToken Success", HttpStatus.OK.value());
    }
}