package com.sparta.avengers.account.repository;


import com.example.intermediate.domain.*;
import com.sparta.avengers.account.controller.domain.Recomment;
import com.sparta.avengers.account.controller.domain.Recommentlike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Member;
import java.util.List;

public interface RecommentlikeRepository extends JpaRepository<Recommentlike, Long> {
    List<Recommentlike> findAllByMember(Member member);
    List<Recommentlike> findAllByRecomment(Recomment recomment);

}