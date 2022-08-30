package com.sparta.avengers.validator;

import com.sparta.avengers.entity.Board;

public class  ValidateObject  {


    public static void  postValidate(Board board){
        if(board.getId()==null || board.getId()<=0){
            throw new IllegalArgumentException("유효하지 않는 Board Id입니다.");
        }
    }
}
