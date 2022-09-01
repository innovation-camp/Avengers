package com.sparta.avengers.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyLikeBoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private String imgURL;
    private String author;
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
