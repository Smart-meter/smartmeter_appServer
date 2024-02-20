package org.cmpe295.meterimage.controller;

import org.cmpe295.meterimage.model.MeterReadingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.cmpe295.meterimage.service.MeterImageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/meter-images")
public class MeterImagesController {
    @Autowired
    private MeterImageService meterImagesService;
    private static final Logger logger = LoggerFactory.getLogger(MeterImagesController.class);
    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadImage(@ModelAttribute MeterReadingRequest request) throws IOException {

            logger.info("Received image upload request: {}", request);
            Integer predictedReadingValue = meterImagesService.uploadImage(request);
            logger.info("Image uploaded successfully");
            return ResponseEntity.ok(predictedReadingValue);


    }
    @PostMapping("/testmeterimageupload")
    public ResponseEntity<String> testMeterImage(
            @RequestParam("readingValue") Integer readingValue,
            @RequestParam("utilityAccountNumber") Long utilityAccountNumber) {

        try {
            // For testing purposes, you can log the received parameters
            System.out.println("Reading Value: " + readingValue);
            System.out.println("Utility Account Number: " + utilityAccountNumber);

            // Your test logic here

            return ResponseEntity.ok("Test successful");
        } catch (Exception e) {
            // Log exception
            // ...

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Test failed");
        }
    }

}
