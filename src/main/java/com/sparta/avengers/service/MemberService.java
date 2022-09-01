package com.sparta.avengers.service;

import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.MemberRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;

import javax.servlet.http.HttpServletResponse;

public interface MemberService {
    GlobalResDto signup(MemberRequestDto accountReqDto);

    GlobalResDto login(LoginRequestDto loginReqDot, HttpServletResponse response);
}