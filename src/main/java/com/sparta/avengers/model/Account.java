package com.sparta.avengers.model;

import com.sparta.avengers.dto.request.AccountRequestDto;
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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    private String nickname;

    public Account() {}

    public Account(AccountRequestDto accountReqDto) {
        this.userId = accountReqDto.getUserId();
        this.password = accountReqDto.getPassword();
        this.nickname = accountReqDto.getNickname();
    }

    public Account(String encodedPassword, String userId, String  nickname) {
        this.password = encodedPassword;
        this.userId = userId;
        this.nickname = nickname;
    }
}