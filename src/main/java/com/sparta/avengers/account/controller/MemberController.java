package com.sparta.avengers.account.controller;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.sparta.avengers.account.controller.request.LoginRequestDto;
import com.sparta.avengers.account.controller.request.MemberRequestDto;
import com.sparta.avengers.account.controller.response.ResponseDto;
import com.sparta.avengers.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @RequestMapping(value = "/api/member/signup", method = RequestMethod.Board)
    public ResponseDto<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {

        return memberService.createMember(requestDto);
    }

    @RequestMapping(value = "/api/member/login", method = RequestMethod.Board)
    public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto,
                                HttpServletResponse response
    ) {
        return memberService.login(requestDto, response);
    }


    @RequestMapping(value = "/api/auth/member/logout", method = RequestMethod.Board)
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }
}