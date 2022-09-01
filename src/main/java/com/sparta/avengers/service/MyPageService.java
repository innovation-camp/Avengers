package com.sparta.avengers.service;

import com.sparta.avengers.dto.response.*;
import com.sparta.avengers.entity.*;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final CommentRepository commentRepository;
    private final NestedCommentRepository nestedCommentRepository;
    private final BoardRepository boardRepository;
    private final BoardlikeRepository boardlikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseDto<MyPageResponseDto> getMyPage(HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (member == null) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        List<Board> boardList = boardRepository.findByMember(member);
        List<MyPageBoardResponseDto> boardResponseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            boardResponseDtoList.add(
                    MyPageBoardResponseDto.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .content(board.getContent())
                            .author(board.getMember().getName())
                            .likes(board.getLikes())
                            .createdAt(board.getCreatedAt())
                            .modifiedAt(board.getModifiedAt())
                            .build()
            );
        }

        List<Comment> commentList = commentRepository.findByMember(member);
        List<MyPageCommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    MyPageCommentResponseDto.builder()
                            .id(comment.getId())
                            .writer(comment.getMember().getName())
                            .content(comment.getContent())
                            .likes(comment.getLikes())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        List<NestedComment> nestedCommentList = nestedCommentRepository.findAllByMember(member);
        List<MyPageNestedCmtResponseDto> nestedCmtResponseDtoList = new ArrayList<>();
        for (NestedComment nestedComment : nestedCommentList) {
            nestedCmtResponseDtoList.add(
                    MyPageNestedCmtResponseDto.builder()
                            .id(nestedComment.getId())
                            .writer(nestedComment.getMember().getName())
                            .content(nestedComment.getContent())
                            .likes(nestedComment.getLikes())
                            .createdAt(nestedComment.getCreatedAt())
                            .modifiedAt(nestedComment.getModifiedAt())
                            .build()
            );
        }

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto();
        myPageResponseDto.update(boardResponseDtoList, commentResponseDtoList, nestedCmtResponseDtoList);
        return ResponseDto.success(myPageResponseDto);
    }


    @Transactional
    public ResponseDto<MyLikePageResponseDto> getMyLikePage(HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (member == null) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        List<CommentLike> commentLikeList = commentLikeRepository.findAllLikeByMember(member);
        List<MyLikeCmtResponseDto> myLikeCmtResponseDtoList = new ArrayList<>();

        for (CommentLike commentLike : commentLikeList) {
            myLikeCmtResponseDtoList.add(
                    MyLikeCmtResponseDto.builder()
                            .id(commentLike.getComment().getId())
                            .writer(commentLike.getMember().getName())
                            .content(commentLike.getComment().getContent())
                            .likes(commentLike.getComment().getLikes())
                            .createdAt(commentLike.getComment().getCreatedAt())
                            .modifiedAt(commentLike.getComment().getModifiedAt())
                            .build()
            );
        }

        List<Boardlike> boardLikeList = boardlikeRepository.getByMember(member);
        List<MyLikeBoardResponseDto> myLikeBoardResponseDtoList = new ArrayList<>();

        for(Boardlike boardlike : boardLikeList) {
            myLikeBoardResponseDtoList.add(
                    MyLikeBoardResponseDto.builder()
                            .id(boardlike.getBoard().getId())
                            .title(boardlike.getBoard().getTitle())
                            .imgURL(boardlike.getBoard().getImgURL())
                            .content(boardlike.getBoard().getContent())
                            .author(boardlike.getMember().getName())
                            .likes(boardlike.getBoard().getLikes())
                            .createdAt(boardlike.getBoard().getCreatedAt())
                            .modifiedAt(boardlike.getBoard().getModifiedAt())
                            .build()
            );
        }
        MyLikePageResponseDto myLikePageResponseDto = new MyLikePageResponseDto();
        myLikePageResponseDto.update(myLikeBoardResponseDtoList, myLikeCmtResponseDtoList);
        return ResponseDto.success(myLikePageResponseDto);
    }


    // 멤버 인증
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!jwtUtil.tokenValidation(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return jwtUtil.getMemberFromAuthentication();
    }
}
