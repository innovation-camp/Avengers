package com.sparta.avengers.account.repository;


import com.example.intermediate.domain.*;
import com.sparta.avengers.account.controller.domain.Post;
import com.sparta.avengers.account.controller.domain.Postlike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Member;
import java.util.List;

public interface PostlikeRepository extends JpaRepository<Postlike, Long> {
    List<Postlike> findAllByMember(Member member);
    List<Postlike> findAllByPost(Post post);

}