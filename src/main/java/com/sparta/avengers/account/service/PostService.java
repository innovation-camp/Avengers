package com.sparta.avengers.account.service;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.repository.PostlikeRepository;
import com.sparta.avengers.account.controller.domain.Comment;
import com.sparta.avengers.account.controller.domain.Member;
import com.sparta.avengers.account.controller.domain.Post;
import com.sparta.avengers.account.controller.domain.Postlike;
import com.sparta.avengers.account.controller.request.PostRequestDto;
import com.sparta.avengers.account.controller.response.CommentResponseDto;
import com.sparta.avengers.account.controller.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostlikeRepository postlikeRepository;
  private final FileUploadService fileUploadService;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createPost(MultipartFile multipartFile, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);//request로 온 토큰으로 멤버 객체 반환
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .likenum(0)
            .Url(fileUploadService.uploadImage(multipartFile))
            .member(member)
            .build();
    postRepository.save(post);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(post.getMember().getNickname())
                    .Url(post.getUrl())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }


  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentResponseDtoList(commentResponseDtoList)
                    .author(post.getMember().getNickname())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostResponseDto> responseDtos = new ArrayList<PostResponseDto>();
    for (Post post : postList) {
      responseDtos.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .commentResponseDtoList(commentByPost(post,post.getMember()))
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(responseDtos);
  }

  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  public List<CommentResponseDto> commentByPost(Post post, Member member)
  {
    List<Comment> commentList= commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtos=new ArrayList<>();

    for(Comment comment: commentList)
    {
      commentResponseDtos.add(
              CommentResponseDto
                      .builder()
                      .id(comment.getId())
                      .author(member.getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    return commentResponseDtos;

  }

  @Transactional
  public ResponseDto<?> likePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }
    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }


    List<Postlike> postlikes=postlikeRepository.findAllByPost(post);
    boolean check=false;
    for(Postlike postlike:postlikes)
    {
      if(postlike.getMember().equals(member))
      {
        check=true;
        System.out.println("이미 좋아요한 게시물입니다.");
        post.pushDislike();
        postlikeRepository.delete(postlike);
        break;
      }
    }
    if(!check)
    {
      post.pushLike();
      System.out.println("좋아요.");
      Postlike postlike= Postlike.builder()
              .member(member)
              .post(post)
              .build();
      postlikeRepository.save(postlike);
    }

    return ResponseDto.success("Push 'like' button");
  }




}