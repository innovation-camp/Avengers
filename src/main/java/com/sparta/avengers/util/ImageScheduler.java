package com.sparta.avengers.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sparta.avengers.entity.Image;
import com.sparta.avengers.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ImageScheduler {

    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.url}")
    private String path;

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteImage() throws InterruptedException{

        //s3Client에 요청 보낼 내용정리
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 요청결과 객체
        ObjectListing s3Objects;


        listObjectsRequest.setBucketName(bucket);
        listObjectsRequest.setPrefix("static/");
        do{
            s3Objects = amazonS3Client.listObjects( listObjectsRequest);
            System.out.println(s3Objects.getObjectSummaries());

            for(S3ObjectSummary s3ObjectSummary : s3Objects.getObjectSummaries() ){

                String file = s3ObjectSummary.getKey();
                //db에 저장된 경로를 만들고, 확인
                try{
                    String fileName = (file.split("static/"))[1];
                    String imgUrl = path+fileName;
                    Image image = imageRepository.findByImgURL(imgUrl).orElse(null);
                    //해당경로가 없을 경우, 해당 파일 삭제해야함 s3에서
                    if(image==null){
                        deleteS3Image(file);
                    }
                }
                catch (Exception e){
                }
            }
            //1000개 이상일 때 체크;
            listObjectsRequest.setMarker(s3Objects.getNextMarker());
        }while(s3Objects.isTruncated());
    }

    public void deleteS3Image(String file){
        System.out.println("파일삭제 : "+file);
        amazonS3Client.deleteObject(bucket,file);
    }
}
