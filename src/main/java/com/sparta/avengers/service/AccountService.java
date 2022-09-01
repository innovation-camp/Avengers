package com.sparta.avengers.service;

import com.sparta.avengers.dto.global.dto.GlobalResDto;
import com.sparta.avengers.dto.request.AccountRequestDto;
import com.sparta.avengers.dto.request.LoginRequestDto;

import javax.servlet.http.HttpServletResponse;

public interface AccountService {
    GlobalResDto signup(AccountRequestDto accountReqDto);

    GlobalResDto login(LoginRequestDto loginReqDot, HttpServletResponse response);
}