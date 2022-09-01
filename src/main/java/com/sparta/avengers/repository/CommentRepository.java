package com.sparta.avengers.repository;

import com.sparta.avengers.entity.Board;
import com.sparta.avengers.entity.Comment;
import com.sparta.avengers.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoard(Board board);
    List<Comment> findByMember(Member member);

    void deleteByBoardId(Long id);
}
