package com.sparta.avengers.repository;

import com.sparta.avengers.model.Comment;
import com.sparta.avengers.model.Member;
import com.sparta.avengers.model.NestedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NestedCommentRepository extends JpaRepository<NestedComment, Long> {
    List<NestedComment> findAllByComment(Comment comment);
    List<NestedComment> findAllByMember(Member member);

    void deleteByBoardId(Long id);
}
