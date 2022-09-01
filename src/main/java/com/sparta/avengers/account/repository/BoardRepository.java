package com.sparta.avengers.account.repository;

import com.example.intermediate.domain.Board;
import com.sparta.avengers.account.controller.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
  List<Board> findAllByOrderByUpdatedAtDesc();
}
