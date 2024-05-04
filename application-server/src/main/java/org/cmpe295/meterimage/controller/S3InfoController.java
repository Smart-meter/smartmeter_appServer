package org.cmpe295.meterimage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meter-images")
public class S3InfoController {

    private final String s3BucketName;

    @Autowired
    public S3InfoController(String s3BucketName) {
        this.s3BucketName = s3BucketName;
    }

    @GetMapping("/s3-bucket-name")
    public String getS3BucketName() {
        return s3BucketName;
    }
}
