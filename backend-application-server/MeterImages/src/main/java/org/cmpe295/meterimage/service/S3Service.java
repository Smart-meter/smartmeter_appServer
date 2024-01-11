package org.cmpe295.meterimage.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
public class S3Service {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public S3Service(AWSCredentialsProvider awsCredentialsProvider) {
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion("your-region") // Specify your AWS region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider.getCredentials()))
                .build();
    }

    public String uploadFile(MultipartFile file, Long utilityAccountNumber) throws IOException {
        String key = utilityAccountNumber + "/" + UUID.randomUUID() + "/" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

        // Get the public URL of the uploaded file
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
