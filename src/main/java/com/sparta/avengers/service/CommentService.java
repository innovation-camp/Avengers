package com.sparta.avengers.service;

import com.sparta.avengers.dto.request.CommentRequestDto;
import com.sparta.avengers.dto.response.CommentResponseDto;
import com.sparta.avengers.dto.response.NestedCommentResponseDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.model.Board;
import com.sparta.avengers.model.Comment;
import com.sparta.avengers.model.NestedComment;
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
    // tokenProvider 선언
    // boardService 선언


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
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    // 해당 id 댓글 유무 확인
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
}
