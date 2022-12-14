package com.sparta.avengers.service;

import com.sparta.avengers.entity.*;
import com.sparta.avengers.dto.request.CommentRequestDto;
import com.sparta.avengers.dto.response.CommentResponseDto;
import com.sparta.avengers.dto.response.NestedCommentResponseDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.entity.Board;
import com.sparta.avengers.entity.Comment;
import com.sparta.avengers.entity.NestedComment;
import com.sparta.avengers.jwt.util.JwtUtil;
import com.sparta.avengers.repository.CommentLikeRepository;
import com.sparta.avengers.repository.CommentRepository;
import com.sparta.avengers.repository.NestedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NestedCommentRepository nestedCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BoardService boardService;
    private final JwtUtil jwtUtil;


    @Transactional(readOnly = true)
    public ResponseDto<?> getCommentsByBoard(Long boardId) {
        Board board = boardService.isPresentBoard(boardId); // 메서드 이름 확인
        if (board == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.");
        }

        List<Comment> commentList = commentRepository.findAllByBoard(board);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            List<NestedComment> nestedCommentList = nestedCommentRepository.findAllByComment(comment);
            List<NestedCommentResponseDto> nestedCommentResponseDtoList = new ArrayList<>();

            for (NestedComment nestedComment : nestedCommentList) {
                nestedCommentResponseDtoList.add(
                        NestedCommentResponseDto.builder()
                                .id(nestedComment.getId())
                                .writer(nestedComment.getMember().getName())
                                .content(nestedComment.getContent())
                                .likes(nestedComment.getLikes())
                                .createdAt(nestedComment.getCreatedAt())
                                .modifiedAt(nestedComment.getModifiedAt())
                                .build()
                );
            }

            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .writer(comment.getMember().getName())
                            .content(comment.getContent())
                            .likes(comment.getLikes())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .nestedCommentResponseDtoList(nestedCommentResponseDtoList)
                            .build()
            );
        }

        return ResponseDto.success(commentResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {

        // 토큰 및 권한 확인 작성칸

        Member member = validateMember(request);
        if (member == null) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Board board = boardService.isPresentBoard(commentRequestDto.getBoardId()); // 메서드 이름 확인
        if (board == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.");
        }

        Comment comment = Comment.builder()
                .member(member)
                .board(board)
                .content(commentRequestDto.getContent())
                .build();
        commentRepository.save(comment);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .writer(comment.getMember().getName())
                        .content(comment.getContent())
                        .likes(comment.getLikes())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {

        // 토큰 및 권한 확인 작성칸

        Member member = validateMember(request);
        if (member == null) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Board board = boardService.isPresentBoard(commentRequestDto.getBoardId()); // 메서드 이름 확인
        if (board == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 입니다.");
        }
        
        Comment comment = isPresentComment(id);
        if (comment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }
        
        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }
        
        comment.update(commentRequestDto);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .writer(comment.getMember().getName())
                        .content(comment.getContent())
                        .likes(comment.getLikes())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {

        // 토큰 및 권한 확인 작성칸

        Member member = validateMember(request);
        if (member == null) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = isPresentComment(id);
        if (comment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return ResponseDto.success("success");
    }


    // 멤버 인증
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!jwtUtil.tokenValidation(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return jwtUtil.getMemberFromAuthentication();
    }

    // 해당 id 댓글 유무 확인
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    @Transactional
    public ResponseDto<?> likeComment(Long id, HttpServletRequest request) {
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
        Comment comment = isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }


        List<CommentLike> commentLikeList = commentLikeRepository.findAllByComment(comment);
        boolean check=false;
        for(CommentLike commentLike: commentLikeList)
        {
            if(commentLike.getMember().equals(member))
            {
                check=true;
                System.out.println("이미 좋아요한 댓글입니다.");
                comment.pushDislike();
                commentLikeRepository.delete(commentLike);
                break;
            }
        }
        if(!check)
        {
            comment.pushLike();
            System.out.println("좋아요.");
            CommentLike commentLike = CommentLike.builder()
                    .member(member)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
        }

        return ResponseDto.success("Push 'like' button");
    }
}
