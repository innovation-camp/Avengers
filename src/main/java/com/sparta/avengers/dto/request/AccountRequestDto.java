package com.sparta.avengers.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AccountRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    private String nickname;
}