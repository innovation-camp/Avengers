package com.sparta.avengers.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String password;
//    private String nickname;
}