package com.sparta.avengers.account.repository;

import com.example.intermediate.domain.Post;
import com.sparta.avengers.account.controller.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByUpdatedAtDesc();
}
