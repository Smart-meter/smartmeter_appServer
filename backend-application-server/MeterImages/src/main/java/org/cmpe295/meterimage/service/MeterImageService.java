package org.cmpe295.meterimage.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.cmpe295.meterimage.model.MeterReadingRequest;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.repository.MeterReadingRepository;
import org.cmpe295.user.repository.UtilityAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class MeterImageService {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private MeterReadingRepository meterReadingRepository;
    @Autowired
    private UtilityAccountRepository utilityAccountRepository;

    private static final Logger logger = LoggerFactory.getLogger(MeterImageService.class);

    public String uploadImage(MeterReadingRequest request) throws IOException {
        // Validate utilityAccountNumber, check if the account exists, etc.
        Optional<UtilityAccount> utilityAccount = utilityAccountRepository.findByUtilityAccountNumber(request.getUtilityAccountNumber());
        if (utilityAccount == null) {
            logger.error("Utility Account Not Found");
            throw new EntityNotFoundException("UtilityAccount not found for utility account number: " + request.getUtilityAccountNumber());
        }
        MeterReading meterReading = new MeterReading();
        meterReading.setUtilityAccount(utilityAccount.get());
        // Set other properties as needed, e.g., dateOfReading, readingValue, etc.
        meterReading.setDateOfReading(LocalDate.now());
        // Upload image to S3
        logger.info("Uploading the image file to S3 bucket");
        String imageUrl = s3Service.uploadFile(request.getImageFile(), request.getUtilityAccountNumber());
        // Save the URL in the MeterReading entity
        meterReading.setImageURL(imageUrl);
        // Save the MeterReading entity to the database
        meterReadingRepository.save(meterReading);
        logger.info("Meter Reading entry saved");
        return imageUrl;
    }
}
