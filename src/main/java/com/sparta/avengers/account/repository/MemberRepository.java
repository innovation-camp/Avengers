package com.sparta.avengers.account.repository;

import com.example.intermediate.domain.Member;
import com.sparta.avengers.account.controller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long id);
  Optional<Member> findByName(String name);
}
