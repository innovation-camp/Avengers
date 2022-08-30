package com.sparta.avengers.validator;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class URLvalidator {

    public static boolean isValidURL(String url){

        try{
            new URL(url).toURI();
            return true;
        }catch (URISyntaxException exception){
            throw new IllegalArgumentException("imgUrl 이 유효하지 않습니다");
        }catch (MalformedURLException exception){
            throw new IllegalArgumentException("imgUrl 이 유효하지 않습니다");
        }

    }
}
