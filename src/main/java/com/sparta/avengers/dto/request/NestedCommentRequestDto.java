package com.sparta.avengers.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NestedCommentRequestDto {
    private Long commentId;
    private String content;
}
