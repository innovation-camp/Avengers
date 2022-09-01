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
public class MyLikePageResponseDto {
    private List likeBoardList;
    private List likeCommentList;

    public void update(
            List<MyLikeBoardResponseDto> myPageBoardResponseDtoList,
            List<MyLikeCmtResponseDto> myPageCommentResponseDtoList) {
        this.likeBoardList = myPageBoardResponseDtoList;
        this.likeCommentList = myPageCommentResponseDtoList;}
}
