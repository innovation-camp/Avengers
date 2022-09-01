package com.sparta.avengers.account.repository;


import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Commentlike;
import com.example.intermediate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentlikeRepository extends JpaRepository<Commentlike, Long> {
    List<Commentlike> findAllByMember(Member member);
    List<Commentlike> findAllByComment(Comment comment);

}