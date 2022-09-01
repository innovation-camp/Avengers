package com.sparta.avengers.account.controller;

import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.CommentService;
import com.example.intermediate.service.RecommentService;
import com.sparta.avengers.account.controller.request.CommentRequestDto;
import com.sparta.avengers.account.controller.response.ResponseDto;
import com.sparta.avengers.account.service.RecommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class RecommentController {


    private final RecommentService recommentService;

    @RequestMapping(value = "/api/auth/recomment", method = RequestMethod.POST)
    public ResponseDto<?> createRecomment(@RequestBody CommentRequestDto requestDto,
                                          HttpServletRequest request) {
        return recommentService.createRecomment(requestDto, request);
    }

    @RequestMapping(value = "/api/auth/recomment/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return recommentService.updateRecomment(id, requestDto, request);
    }

    @RequestMapping(value = "/api/auth/recomment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return recommentService.deleteRecomment(id, request);
    }

    @RequestMapping(value = "/api/auth/recommentlike/{id}", method = RequestMethod.POST)
    public ResponseDto<?> likeRecomment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return recommentService.likeRecomment(id, request);
    }
}