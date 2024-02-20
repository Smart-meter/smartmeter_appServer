package org.cmpe295.meterimage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
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
        //Get the predicted Meter Reading
        //logger.info("Calling the inference endpoint to get meter reading predction");
        //Integer predictedReadingValue = getPredictedReadingValue(request.getImageFile());
        //meterReading.setReadingValue(predictedReadingValue);
        // Save the MeterReading entity to the database
        meterReadingRepository.save(meterReading);
        logger.info("Meter Reading entry saved");
        return imageUrl;
    }

    private Integer getPredictedReadingValue(MultipartFile imageFile) {
        logger.info("Entered the prediction method");
        //Use the URL from prediction endpoint here
        String predictEndpoint = "http://127.0.0.1:8001/predict";
        logger.info(predictEndpoint);

        // Create the RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Use InputStreamResource to handle binary data
        InputStreamResource imageResource;
        try {
            imageResource = new InputStreamResource(imageFile.getInputStream());
        } catch (IOException e) {
            logger.error("Error reading the image file");
            throw new RuntimeException(e);
        }

        try {
            // Use MultiValueMap to handle the multipart request without manually setting the boundary
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new HttpEntity<>(imageResource, headers));

            // Set the body and headers in the requestEntity
            RequestEntity<MultiValueMap<String, Object>> requestEntity =
                    new RequestEntity<>(body, headers, HttpMethod.POST, new URI(predictEndpoint));

            logger.info("Request Entity formed");

            // Send the request and get the response
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);

            logger.info("Hit the prediction endpoint and response received");

            // Check if the request was successful (HTTP status code 200)
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Parse the response to extract the predicted reading value
                byte[] responseBodyBytes = responseEntity.getBody();
                // Specify the correct encoding when decoding the byte sequence
                String responseBodyString = new String(responseBodyBytes, StandardCharsets.UTF_8);
                Integer predictedReadingValue = parseResponse(responseBodyString);
                logger.info(predictedReadingValue.toString() + " is the predicted reading");
                return predictedReadingValue;
            } else {
                // Handle non-successful response (e.g., log error, throw exception)
                System.err.println("Prediction request failed with status code: " + responseEntity.getStatusCode());
                logger.info("Prediction Request Failed");
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., log error, throw exception)
            e.printStackTrace();
            logger.error("Error during prediction request");
        }

        // Return a default value or handle the error case
        return null;
    }

    // Method to parse the response and extract the predicted reading value
    private Integer parseResponse(String responseBody) {
        // Parse the JSON response and extract the "meter_reading" field

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = null;
        try {
            responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String meterReading = (String) responseMap.get("meter_reading");
        return Integer.parseInt(meterReading);
    }
}
