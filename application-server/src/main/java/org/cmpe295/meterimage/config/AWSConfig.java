package org.cmpe295.meterimage.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.bucket-name}")
    private String s3BucketName;
    @Value("${prediction.url}")
    private String predictionUrl;
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }
    @Bean
    public String awsRegion() {
        return awsRegion;
    }

    @Bean
    public String s3BucketName() {
        return s3BucketName;
    }

    @Bean
    public String predictionUrl(){
        return predictionUrl;
    }
}
