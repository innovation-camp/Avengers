package com.sparta.avengers.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    private List boardList;
    private List commentList;
    private List nestedCommentList;

    public void update(
            List<MyPageBoardResponseDto> boardResponseDtoList,
            List<MyPageCommentResponseDto> commentResponseDtoList,
            List<MyPageNestedCmtResponseDto> nestedCmtResponseDtoList) {
        this.boardList = boardResponseDtoList;
        this.commentList = commentResponseDtoList;
        this.nestedCommentList = nestedCmtResponseDtoList;
    }
}
