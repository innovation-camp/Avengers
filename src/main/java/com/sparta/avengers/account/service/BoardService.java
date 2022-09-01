package com.sparta.avengers.account.service;

import com.example.intermediate.controller.request.BoardRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.BoardResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.BoardRepository;
import com.example.intermediate.repository.BoardlikeRepository;
import com.sparta.avengers.account.controller.domain.Comment;
import com.sparta.avengers.account.controller.domain.Member;
import com.sparta.avengers.account.controller.domain.Board;
import com.sparta.avengers.account.controller.domain.Boardlike;
import com.sparta.avengers.account.controller.request.BoardRequestDto;
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
public class BoardService {

  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;
  private final BoardlikeRepository BoardlikeRepository;
  private final FileUploadService fileUploadService;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createBoard(MultipartFile multipartFile, BoardRequestDto requestDto, HttpServletRequest request) {
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

    Board board = Board.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .likenum(0)
            .Url(fileUploadService.uploadImage(multipartFile))
            .member(member)
            .build();
    boardRepository.save(board);
    return ResponseDto.success(
            boardResponseDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .author(board.getMember().getName())
                    .Url(board.getUrl())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build()
    );
  }


  @Transactional(readOnly = true)
  public ResponseDto<?> getBoard(Long id) {
    Board board = isPresentBoard(id);
    if (null == board) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByBoard(board);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getName())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .updatedAt(comment.getUpdatedAt())
                      .build()
      );
    }

    return ResponseDto.success(
            BoardResponseDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .commentResponseDtoList(commentResponseDtoList)
                    .author(board.getMember().getName())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllBoard() {
    List<Board> boardList = BoardRepository.findAllByOrderByUpdatedAtDesc();
    List<BoardResponseDto> responseDtos = new ArrayList<BoardResponseDto>();
    for (Board board : boardList) {
      responseDtos.add(
              BoardResponseDto.builder()
                      .id(board.getId())
                      .title(board.getTitle())
                      .content(board.getContent())
                      .author(board.getMember().getName())
                      .commentResponseDtoList(commentByBoard(board, board.getMember()))
                      .createdAt(board.getCreatedAt())
                      .updatedAt(board.getUpdatedAt())
                      .build()
      );
    }
    return ResponseDto.success(responseDtos);
  }

  @Transactional
  public ResponseDto<Board> updateBoard(Long id, BoardRequestDto requestDto, HttpServletRequest request) {
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

    Board board = isPresentBoard(id);
    if (null == board) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (board.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    board.update(requestDto);
    return ResponseDto.success(board);
  }

  @Transactional
  public ResponseDto<?> deleteBoard(Long id, HttpServletRequest request) {
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

    Board board = isPresentBoard(id);
    if (null == board) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (board.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    BoardRepository.delete(board);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Board isPresentBoard(Long id) {
    Optional<Board> optionalBoard = BoardRepository.findById(id);
    return optionalBoard.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  public List<CommentResponseDto> commentByBoard(Board board, Member member)
  {
    List<Comment> commentList= commentRepository.findAllByBoard(board);
    List<CommentResponseDto> commentResponseDtos=new ArrayList<>();

    for(Comment comment: commentList)
    {
      commentResponseDtos.add(
              CommentResponseDto
                      .builder()
                      .id(comment.getId())
                      .author(member.getName())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .updatedAt(comment.getUpdatedAt())
                      .build()
      );
    }

    return commentResponseDtos;

  }

  @Transactional
  public ResponseDto<?> likeBoard(Long id, HttpServletRequest request) {
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
    Board board = isPresentBoard(id);
    if (null == board) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }


    List<Boardlike> Boardlikes=BoardlikeRepository.findAllByBoard(board);
    boolean check=false;
    for(Boardlike Boardlike:Boardlikes)
    {
      if(Boardlike.getMember().equals(member))
      {
        check=true;
        System.out.println("이미 좋아요한 게시물입니다.");
        board.pushDislike();
        BoardlikeRepository.delete(Boardlike);
        break;
      }
    }
    if(!check)
    {
      board.pushLike();
      System.out.println("좋아요.");
      Boardlike Boardlike= Boardlike.builder()
              .member(member)
              .Board(board)
              .build();
      BoardlikeRepository.save(Boardlike);
    }

    return ResponseDto.success("Push 'like' button");
  }




}