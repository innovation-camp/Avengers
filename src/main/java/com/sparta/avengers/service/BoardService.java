package com.sparta.avengers.service;

import com.sparta.avengers.dto.ImageResponseDto;
import com.sparta.avengers.dto.request.BoardRequestDto;
import com.sparta.avengers.dto.response.BoardResponseDto;
import com.sparta.avengers.dto.response.CommentResponseDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.entity.Board;
import com.sparta.avengers.entity.Boardlike;
import com.sparta.avengers.entity.Comment;
import com.sparta.avengers.entity.Member;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.repository.BoardRepository;
import com.sparta.avengers.repository.BoardlikeRepository;
import com.sparta.avengers.repository.CommentRepository;
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
  private final S3UploaderService s3UploaderService;
  private final JwtUtil jwtUtil;

  @Transactional
  public ResponseDto<?> createBoard(BoardRequestDto requestDto, MultipartFile multipartFile, HttpServletRequest request) {
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
    //AWS
    String FileName = null;
    if (multipartFile.isEmpty()) {
      return ResponseDto.fail("INVALID_FILE", "파일이 유효하지 않습니다.");
    }
    ImageResponseDto imageResponseDto = null;
    try {
      FileName = s3UploaderService.uploadFiles(multipartFile, "image");
      imageResponseDto = new ImageResponseDto(FileName);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assert imageResponseDto != null;
    Board board = Board.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .likes(0L)
            .imgURL(imageResponseDto.getImageUrl())
            .member(member)
            .build();
    boardRepository.save(board);

    return ResponseDto.success(
            BoardResponseDto.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .author(board.getMember().getName())
                    .url(board.getImgURL())
                    .createdAt(board.getCreatedAt())
                    .modifiedAt(board.getModifiedAt())
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
                      .writer(comment.getMember().getName())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
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
                    .modifiedAt(board.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllBoard() {
    List<Board> boardList = boardRepository.findAllByOrderByModifiedAtDesc();
    List<BoardResponseDto> boardResponseDto = new ArrayList<>();
    for (Board board : boardList) {
      boardResponseDto.add(
              BoardResponseDto.builder()
                      .id(board.getId())
                      .title(board.getTitle())
                      .content(board.getContent())
                      .author(board.getMember().getName())
                      .commentResponseDtoList(commentByBoard(board, board.getMember()))
                      .createdAt(board.getCreatedAt())
                      .modifiedAt(board.getModifiedAt())
                      .build()
      );
    }
    return ResponseDto.success(boardResponseDto);
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

    boardRepository.delete(board);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Board isPresentBoard(Long id) {
    Optional<Board> optionalBoard = boardRepository.findById(id);
    return optionalBoard.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!jwtUtil.tokenValidation(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return jwtUtil.getMemberFromAuthentication();
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
                      .writer(comment.getMember().getName())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
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


    List<Boardlike> boardlikes=BoardlikeRepository.findAllByBoard(board);
    boolean check=false;
    for(Boardlike boardlike:boardlikes)
    {
      if(boardlike.getMember().equals(member))
      {
        check=true;
        System.out.println("이미 좋아요한 게시물입니다.");
        board.pushDislike();
        BoardlikeRepository.delete(boardlike);
        break;
      }
    }
    if(!check)
    {
      board.pushLike();
      System.out.println("좋아요.");
      Boardlike boardlike = Boardlike.builder()
              .member(member)
              .board(board)
              .build();
      BoardlikeRepository.save(boardlike);
    }

    return ResponseDto.success("Push 'like' button");
  }




}