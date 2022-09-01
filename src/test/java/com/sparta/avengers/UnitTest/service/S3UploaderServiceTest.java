package com.sparta.avengers.UnitTest.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.sparta.avengers.config.S3MockConfig;
import com.sparta.avengers.service.S3UploaderService;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(S3MockConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class S3UploaderServiceTest {


    @Autowired
    private AmazonS3Client amazonS3;

    @Autowired
    private S3UploaderService s3UploaderService;

    @Autowired
    private static final String BUCKET_NAME = "hosunghan";
    private File file;
    private final MultipartFile multipartFile = new MockMultipartFile(
            "test1",
            "test1.PNG",
            MediaType.IMAGE_PNG_VALUE,
            "test1".getBytes()
    );

    @BeforeEach
    void setUp(@Autowired S3Mock s3Mock) throws IOException {

        //가짜 S3 만들기 && 가짜 이미지 파일 만들기
        s3Mock.start();
        amazonS3.createBucket(BUCKET_NAME);
        file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);


    }

    @AfterEach
    void shutDown(@Autowired S3Mock s3Mock) {
        if(file.delete()){
            System.out.println("file delete success");
        }
        else{
            System.out.println("File delete fail");
        }
        amazonS3.shutdown();
        s3Mock.stop();
    }

    @Nested
    @DisplayName("이미지 S3 업로드 테스트")
    class upload_success {

        @Test
        @DisplayName("성공")
        void test() throws IOException {
            //given 파일 업로드 한 상황
            String test= s3UploaderService.putS3(file,multipartFile.getOriginalFilename());
            System.out.println(test);
//
//            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,multipartFile.getOriginalFilename(),file)
//                    .withCannedAcl(CannedAccessControlList.PublicRead);
//            amazonS3.putObject(putObjectRequest);
//            // when  업로드된 파일을 가져왔을 때,
//            S3Object s3Object = amazonS3.getObject(BUCKET_NAME,multipartFile.getOriginalFilename());
//
//            //then 같은지 확인
//            //assertThat(file.exists()).isEqualTo(true);
//            assertThat(new String(FileCopyUtils.copyToByteArray(s3Object.getObjectContent()))).isEqualTo("test1");
//


        }
    }

}
