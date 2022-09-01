package com.sparta.avengers.controller;

import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @RequestMapping(value = "/api/mypage", method = RequestMethod.GET)
    ResponseDto<?> getMyPage(HttpServletRequest request) {
        return myPageService.getMyPage(request);
    }

    @RequestMapping(value = "/api/mypage/like", method = RequestMethod.GET)
    ResponseDto<?> getMyLikePage(HttpServletRequest request) {
        return myPageService.getMyLikePage(request);
    }
}
