package com.sparta.avengers.account.controller.response;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;
import javax.servlet.http.HttpServletRequest;

import com.sparta.avengers.account.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController<ResponseDto> {

  private final PostService postService;

  //POST
  @RequestMapping(value = "/api/auth/post", method = RequestMethod.POST)
  public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
      HttpServletRequest request) {
    return postService.createPost(requestDto, request);
  }

  //Get id
  @RequestMapping(value = "/api/post/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  //Get
  @RequestMapping(value = "/api/post", method = RequestMethod.GET)
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  //Put id
  @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }

  //Delete id
  @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
