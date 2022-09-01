package com.sparta.avengers.account.repository;


import com.example.intermediate.domain.*;
import com.sparta.avengers.account.controller.domain.Board;
import com.sparta.avengers.account.controller.domain.Boardlike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Member;
import java.util.List;

public interface BoardlikeRepository extends JpaRepository<Boardlike, Long> {
    List<Boardlike> findAllByMember(Member member);
    List<Boardlike> findAllByBoard(Board board);

}