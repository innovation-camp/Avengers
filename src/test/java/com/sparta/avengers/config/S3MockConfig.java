package com.sparta.avengers.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@TestConfiguration
public class S3MockConfig {

    @Bean(name = "s3Mock")
    public S3Mock s3Mock(){
        return  new S3Mock.Builder()
                .withPort(8001)
                .withInMemoryBackend()
                .build();

    }

    @Primary
    @Bean(name = "amazonS3", destroyMethod = "shutdown")
    public AmazonS3Client amazonS3(){
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration("http://127.0.0.1:8001", Regions.AP_NORTHEAST_2.name());

        AmazonS3Client s3Client =(AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials())).build();
        return s3Client;
    }

}