package com.sparta.avengers.controller;

import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.MemberRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.security.user.UserDetailsImpl;
import com.sparta.avengers.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/api/member/signup")
    public GlobalResDto signup(@RequestBody @Valid MemberRequestDto memberReqDto) {
        return memberService.signup(memberReqDto);
    }

    @PostMapping("/api/member/login")
    public GlobalResDto login(@RequestBody @Valid LoginRequestDto loginReqDot, HttpServletResponse response) {
        return memberService.login(loginReqDot, response);
    }

    @GetMapping("/issue/token")
    public GlobalResDto issuedToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, jwtUtil.createToken(userDetails.getMember().getName(), "Access"));
        return new GlobalResDto("issuedToken Success", HttpStatus.OK.value());
    }
}