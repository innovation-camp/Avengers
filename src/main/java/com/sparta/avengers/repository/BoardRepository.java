package com.sparta.avengers.repository;

import com.sparta.avengers.entity.Board;
import com.sparta.avengers.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
  List<Board> findAllByOrderByModifiedAtDesc();
  List<Board> findByMember(Member member);
}
