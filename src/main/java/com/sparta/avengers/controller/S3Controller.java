package com.sparta.avengers.controller;

import com.sparta.avengers.dto.ResponseDto;
import com.sparta.avengers.service.S3UploaderService;
import com.sparta.avengers.util.ImageScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class S3Controller {
    private final S3UploaderService s3Uploader;
    private final ImageScheduler imageScheduler; // 공통 스케줄러 추가 후 삭제

    //다중이미지 받을 떄
    //List<MultipartFile> files

    @PostMapping("/api/member/image")
    public ResponseDto<?> imageUpload(@RequestParam("image") MultipartFile multipartFile){

        return s3Uploader.getResponseDto(multipartFile);

    }

    @GetMapping("/api/member/test")
    public void test(){
        try{
            imageScheduler.deleteImage();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
