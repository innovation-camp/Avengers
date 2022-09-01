package com.sparta.avengers.entity;

import com.sparta.avengers.dto.request.MemberRequestDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
//    private String nickname;

    public Member() {}

    public Member(MemberRequestDto memberReqDto) {
        this.name = memberReqDto.getName();
        this.password = memberReqDto.getPassword();
//        this.nickname = memberReqDto.getNickname();
    }

    public Member(String encodedPassword, String name) {
        this.password = encodedPassword;
        this.name = name;
//        this.nickname = nickname;
    }
}