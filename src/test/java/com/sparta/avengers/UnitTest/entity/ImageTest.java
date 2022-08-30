package com.sparta.avengers.UnitTest.entity;

import com.sparta.avengers.entity.Image;
import com.sparta.avengers.entity.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @Nested
    @DisplayName("이미지 DB저장 객체 생성")
    class CreateImage{

        private Board board;
        private String imgURL;

        @BeforeEach
        void setup(){
            board = Board.builder()
                    .id(1L)
                    .title("제목")
                    .content("상세내용")
                    .build();

            imgURL="https://hosunghan.s3.ap-northeast-2.amazonaws.com/static/b9a68c15-31bb-42a8-8c18-bbfd96e89edf20220806_193636.jpg";
        }

        @Test
        @DisplayName("정상케이스")
        void createImage_Normal(){

            //given -> setup()

            //when
            Image image = new Image(board,imgURL);

            //then
            assertEquals(board.getId(),image.getBoard().getId());
            assertEquals(imgURL,image.getImgURL());


        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases{
            @Nested
            @DisplayName("BoardId")
            class postId{
                @Test
                @DisplayName("null")
                void failByNull(){
                    //given
                    board.setId(null);
                    //when


                    Exception exception = assertThrows(IllegalArgumentException.class , ()->{
                        new Image(board,imgURL);
                    });
                    // then
                    assertEquals("유효하지 않는 Board Id입니다.",exception.getMessage());
                }
                @Test
                @DisplayName("minus")
                void failByMinus(){

                    //given
                    board.setId(-1L);
                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class , ()->{
                        new Image(board,imgURL);
                    });
                    // then
                    assertEquals("유효하지 않는 Post Id입니다.",exception.getMessage());
                }
            }

            @Nested
            @DisplayName("ImgUrl")
            class ImgUrl{
                @Test
                @DisplayName("ImgUrl : null")
                void failByUrl(){

                    //given
                    imgURL=null;

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class , ()->{
                        new Image(board,imgURL);
                    });
                    //then
                    assertEquals("imgUrl 이 유효하지 않습니다",exception.getMessage());
                }

                @Test
                @DisplayName("ImgUrl : 포맷형태 틀림")
                void failByUrlFormat(){

                    imgURL="shopping-phinf.pstatic.net/main_2416122/24161228524.20200915151118.jpg";
                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class , ()->{
                        new Image(board,imgURL);
                    });
                    //then
                    assertEquals("imgUrl 이 유효하지 않습니다",exception.getMessage());

                }
            }
        }
    }


}
