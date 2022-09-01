package com.sparta.avengers.service;


import com.sparta.avengers.dto.request.NestedCommentRequestDto;
import com.sparta.avengers.dto.response.NestedCommentResponseDto;
import com.sparta.avengers.dto.response.ResponseDto;
import com.sparta.avengers.entity.*;
import com.sparta.avengers.jwt.util.JwtUtil;
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
public class NestedCommentService {
    private final NestedCommentRepository nestedCommentRepository;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;
    private final BoardService boardService;


    @Transactional(readOnly = true)
    public ResponseDto<?> getNestedCommentsByComment(Long commentId) {
        Comment comment = commentService.isPresentComment(commentId);
        if (comment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

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
        return ResponseDto.success(nestedCommentResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> createNestedComment(NestedCommentRequestDto nestedCommentRequestDto, HttpServletRequest request) {

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

        Comment comment = commentService.isPresentComment(nestedCommentRequestDto.getCommentId());
        if (comment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        Board board = boardService.isPresentBoard(comment.getBoard().getId()); // 메서드 이름 확인

        NestedComment nestedComment = NestedComment.builder()
                .member(member)
                .board(board)
                .comment(comment)
                .content(nestedCommentRequestDto.getContent())
                .build();
        nestedCommentRepository.save(nestedComment);
        return ResponseDto.success(
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

    @Transactional
    public ResponseDto<?> updateNestedComment(Long id, NestedCommentRequestDto nestedCommentRequestDto, HttpServletRequest request) {

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

        Comment comment = commentService.isPresentComment(nestedCommentRequestDto.getCommentId());
        if (comment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        NestedComment nestedComment = isPresentNestedComment(id);
        if (nestedComment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        if (nestedComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        nestedComment.update(nestedCommentRequestDto);
        return ResponseDto.success(
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

    @Transactional
    public ResponseDto<?> deleteNestedComment(Long id, HttpServletRequest request) {

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

        NestedComment nestedComment = isPresentNestedComment(id);
        if (nestedComment == null) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 입니다.");
        }

        if (nestedComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        nestedCommentRepository.delete(nestedComment);
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
    public NestedComment isPresentNestedComment(Long id) {
        Optional<NestedComment> optionalNestedComment = nestedCommentRepository.findById(id);
        return optionalNestedComment.orElse(null);
    }
}
