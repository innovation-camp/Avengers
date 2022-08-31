package com.sparta.avengers.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentRequestDto {
    private Long boardId;
    private String content;
}
