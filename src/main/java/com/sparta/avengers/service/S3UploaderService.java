package com.sparta.avengers.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.avengers.dto.ImageResponseDto;
import com.sparta.avengers.dto.ResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@Getter
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFiles(MultipartFile multipartFile , String dirName)throws IOException{
        File uploadFile =convert(multipartFile)
                .orElseThrow(()->new IllegalArgumentException("ERROR :  MultipartFile -> File convert fail"));

        return upload(uploadFile,dirName);
    }

    //(S3 서버에 올리면서 로컬에 만든 파일 지우기) 총괄 함수
    public String upload(File uploadFile, String filepath){
        String fileName = filepath+"/"+UUID.randomUUID() + uploadFile.getName();

        String uploadImageUrl = putS3(uploadFile,fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;

    }

    public ResponseDto<?> getResponseDto(MultipartFile multipartFile) {
        if(multipartFile.isEmpty()){
            return ResponseDto.fail("INVALID_FILE", "파일이 유효하지 않습니다.");
        }
        try{
            return ResponseDto.success(new ImageResponseDto(/*s3Uploader.*/uploadFiles(multipartFile, "static")));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseDto.fail("INVALID_FILE", "파일이 유효하지 않습니다.");
        }
    }


    //S3 업로드
    public String putS3(File uploadFile, String fileName){
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName,uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }

    //로컬에 삭제
    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            System.out.println("File delete success");
            return;
        }
        System.out.println("file delete fail");
    }


    //로컬에 파일 업로드하기
    private Optional<File> convert(MultipartFile file) throws IOException{
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File convertFile = new File(System.getProperty("user.dir") + "/" +now+".jpg" );

        if(convertFile.createNewFile()){
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        System.out.println("변환실패");
        return Optional.empty();
    }

}