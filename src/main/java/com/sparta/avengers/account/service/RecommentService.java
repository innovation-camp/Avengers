package com.sparta.avengers.account.service;

import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.RecommentRepository;
import com.example.intermediate.repository.RecommentlikeRepository;
import com.sparta.avengers.account.controller.domain.Comment;
import com.sparta.avengers.account.controller.domain.Member;
import com.sparta.avengers.account.controller.domain.Recomment;
import com.sparta.avengers.account.controller.domain.Recommentlike;
import com.sparta.avengers.account.controller.request.CommentRequestDto;
import com.sparta.avengers.account.controller.response.CommentResponseDto;
import com.sparta.avengers.account.controller.response.ResponseDto;
import com.sparta.avengers.account.repository.RecommentlikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommentService {

    private final RecommentRepository recommentRepository;
    private final RecommentlikeRepository recommentlikeRepository;
    private final CommentService commentService;
    private final TokenProvider tokenProvider;


    //Exception AccessToken이 없거나, 유효하지 않은 Token일 때 ‘로그인이 필요합니다.’를 200 정상 응답으로 나타내기
    @Transactional
    public ResponseDto<?> createRecomment(CommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //멤버는 httprequest로온 토큰을 이용해 멤버 객체를 찾고 comment 앤티티에 특정 맴버 객체 추가, id 공유됨
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        //comment는 requestbody로 전달받은 commentid로 comment객체 찾아서 recomment 앤티티에 추가해줌. 그 때 id 공유
        Comment comment = commentService.isPresentComment(requestDto.getCommentId());


        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 코멘트입니다.");
        }

        Recomment recomment = Recomment.builder()
                .member(member)
                .comment(comment)
                .content(requestDto.getContent())
                .likenum(0)
                .build();

        recommentRepository.save(recomment);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(recomment.getId())
                        .author(recomment.getMember().getName())
                        .content(recomment.getContent())
                        .createdAt(recomment.getCreatedAt())
                        .updatedAt(recomment.getUpdatedAt())
                        .build()
        );
    }





    @Transactional
    public ResponseDto<?> updateRecomment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
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


        Recomment recomment = isPresentRecomment(id);
        if (null == recomment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 번호 입니다.");
        }

        if (recomment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        recomment.update(requestDto);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(recomment.getId())
                        .author(recomment.getMember().getName())
                        .content(recomment.getContent())
                        .createdAt(recomment.getCreatedAt())
                        .updatedAt(recomment.getUpdatedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteRecomment(Long id, HttpServletRequest request) {
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

        Recomment recomment = isPresentRecomment(id);
        if (null == recomment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (recomment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        recommentRepository.delete(recomment);
        return ResponseDto.success("success");
    }

    @Transactional(readOnly = true)
    public Recomment isPresentRecomment(Long id) {
        Optional<Recomment> optionalRecomment = recommentRepository.findById(id);
        return optionalRecomment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
    @Transactional
    public ResponseDto<?> likeRecomment(Long id, HttpServletRequest request) {
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
        Recomment recomment = isPresentRecomment(id);
        if (null == recomment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }


        List<Recommentlike> recommentlikes=recommentlikeRepository.findAllByRecomment(recomment);
        boolean check=false;
        for(Recommentlike recommentlike:recommentlikes)
        {
            if(recommentlike.getMember().equals(member))
            {
                check=true;
                System.out.println("이미 좋아요한 게시물입니다.");
                recomment.pushDislike();
                recommentlikeRepository.delete(recommentlike);
                break;
            }
        }
        if(!check)
        {
            recomment.pushLike();
            System.out.println("좋아요.");
            Recommentlike recommentlike= Recommentlike.builder()
                    .member(member)
                    .recomment(recomment)
                    .build();
            recommentlikeRepository.save(recommentlike);
        }

        return ResponseDto.success("Push 'like' button");
    }
}