package com.sparta.avengers.controller;

import com.sparta.avengers.dto.request.NestedCommentRequestDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.service.NestedCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Validated
@RestController
public class NestedCommentController {
    private final NestedCommentService nestedCommentService;

    @RequestMapping(value = "/api/nestedcomment/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getNestedCommentsByComment(@PathVariable Long id) {
        return nestedCommentService.getNestedCommentsByComment(id);
    }

    @RequestMapping(value = "/api/auth/nestedcomment", method = RequestMethod.POST)
    public ResponseDto<?> createNestedComment(@RequestBody NestedCommentRequestDto nestedCommentRequestDto,
                                              HttpServletRequest request) {
        return nestedCommentService.createNestedComment(nestedCommentRequestDto, request);
    }

    @RequestMapping(value = "/api/auth/nestedcomment/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateNestedComment(@PathVariable Long id,
                                              @RequestBody NestedCommentRequestDto nestedCommentRequestDto, HttpServletRequest request) {
        return nestedCommentService.updateNestedComment(id, nestedCommentRequestDto, request);
    }

    @RequestMapping(value = "/api/auth/nestedcomment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteNestedComment(@PathVariable Long id, HttpServletRequest request) {
        return nestedCommentService.deleteNestedComment(id, request);
    }
}
