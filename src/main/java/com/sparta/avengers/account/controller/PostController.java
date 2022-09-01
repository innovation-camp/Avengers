package com.sparta.avengers.account.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Post;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.service.FileUploadService;
import com.example.intermediate.service.PostService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    @RequestMapping(value = "/api/auth/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestPart MultipartFile multipartFile,@RequestPart PostRequestDto requestDto,
                                     HttpServletRequest request) {
        return postService.createPost(multipartFile, requestDto, request);
    }

    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @RequestMapping(value = "/api/post", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts() {
        return postService.getAllPost();
    }

    @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(id, postRequestDto, request);
    }

    @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long id,
                                     HttpServletRequest request) {
        return postService.deletePost(id, request);
    }

    @RequestMapping(value = "/api/auth/postlike/{id}", method = RequestMethod.POST)
    public ResponseDto<?> likePost(@PathVariable Long id,
                                   HttpServletRequest request) {
        return postService.likePost(id, request);
    }

}