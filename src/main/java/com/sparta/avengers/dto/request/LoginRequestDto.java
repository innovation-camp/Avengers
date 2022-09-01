package com.sparta.avengers.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequestDto {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
}
