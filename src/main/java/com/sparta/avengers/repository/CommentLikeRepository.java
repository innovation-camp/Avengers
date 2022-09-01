package com.sparta.avengers.repository;

import com.sparta.avengers.entity.Comment;
import com.sparta.avengers.entity.CommentLike;
import com.sparta.avengers.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findAllByMember(Member member);
    List<CommentLike> findAllByComment(Comment comment);
    List<CommentLike> findAllLikeByMember(Member member);
}
