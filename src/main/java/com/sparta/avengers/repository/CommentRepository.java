package com.sparta.avengers.repository;

import com.sparta.avengers.model.Board;
import com.sparta.avengers.model.Comment;
import com.sparta.avengers.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoard(Board board);
    List<Comment> findByMember(Member member);

    void deleteByBoardId(Long id);
}
