package com.sparta.avengers.account.repository;


import com.example.intermediate.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostlikeRepository extends JpaRepository<Postlike, Long> {
    List<Postlike> findAllByMember(Member member);
    List<Postlike> findAllByPost(Post post);

}