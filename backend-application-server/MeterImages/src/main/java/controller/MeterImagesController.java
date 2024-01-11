package controller;

import model.MeterReadingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.MeterImageService;

import java.io.IOException;

@RestController
@RequestMapping("api/meter-images")
public class MeterImagesController {
    @Autowired
    private MeterImageService meterImagesService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@ModelAttribute MeterReadingRequest request) {
        try {
            String imageUrl = meterImagesService.uploadImage(request);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}
