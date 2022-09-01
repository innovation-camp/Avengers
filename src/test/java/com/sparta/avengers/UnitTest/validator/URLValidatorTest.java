package com.sparta.avengers.UnitTest.validator;

import com.sparta.avengers.validator.URLvalidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class URLValidatorTest {

    private String imgUrl;

    @Nested
    @DisplayName("URL Success Test")
    class UrlSuccesTest{

        @Test
        @DisplayName("Success case")
        void success(){
            imgUrl="https://shopping-phinf.pstatic.net/main_8232398/82323985017.4.jpg";
            assertTrue( URLvalidator.isValidURL(imgUrl));
        }
    }

    @Nested
    @DisplayName("URL Fail Test")
    class UrlFailTest{

        @Test
        @DisplayName("URL : Format error")
        void failByFormat(){

            imgUrl="https/";
            //throws를 발생시킬 것이다!
            assertThrows(IllegalArgumentException.class,()->{
                URLvalidator.isValidURL(imgUrl);
            });
        }
        @Test
        @DisplayName("URL : NULL error")
        void failByNull(){

            imgUrl=null;
            //throws를 발생시킬 것
            assertThrows(IllegalArgumentException.class,()->{
                URLvalidator.isValidURL(imgUrl);
            });
            //message 같은 것으로 올바른 메시지가 나오는지 확인할 수도 있다.
        }

        @Test
        @DisplayName("URL : \"\" case ")
        void failByNothing(){

            imgUrl="";
            //throws를 발생시킬 것이다!
            assertThrows(IllegalArgumentException.class,()->{
                URLvalidator.isValidURL(imgUrl);
            });
            //message 같은 것으로 올바른 메시지가 나오는지 확인할 수도 있다.
        }
    }
}
