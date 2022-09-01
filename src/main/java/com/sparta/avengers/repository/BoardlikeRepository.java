package com.sparta.avengers.repository;

import com.sparta.avengers.entity.Board;
import com.sparta.avengers.entity.Boardlike;
import com.sparta.avengers.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardlikeRepository extends JpaRepository<Boardlike, Long> {
    List<Boardlike> findAllByMember(Member member);
    List<Boardlike> findAllByBoard(Board board);
    List<Boardlike> getByMember(Member member);
}