package org.cmpe295.meterimage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cmpe295.meterimage.model.MeterReadingRequest;
import org.cmpe295.user.entity.MeterImageMetadata;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;
import org.cmpe295.user.repository.MeterReadingRepository;
import org.cmpe295.user.repository.UtilityAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeterImageService {
    @Autowired
    private S3Service s3Service;
    @Value("${prediction.url}")
    private String predictEndpoint;

    private final MeterReadingRepository meterReadingRepository;

    private final UtilityAccountRepository utilityAccountRepository;

    private static final Logger logger = LoggerFactory.getLogger(MeterImageService.class);


    public Long uploadImage(MeterReadingRequest request) throws IOException {
        // Validate utilityAccountNumber, check if the account exists, etc.
        Optional<UtilityAccount> utilityAccount = utilityAccountRepository.findByUtilityAccountNumber(
                request.getUtilityAccountNumber());
        if (!utilityAccount.isPresent()) {
            logger.error("Utility Account Not Found");
            throw new EntityNotFoundException("UtilityAccount not found for utility account number: " + request.getUtilityAccountNumber());
        }
        MeterReading meterReading = new MeterReading();
        meterReading.setUtilityAccount(utilityAccount.get());
        // Set other properties as needed, e.g., dateOfReading, readingValue, etc.
        meterReading.setDateOfReading(LocalDateTime.now());
        // Upload image to S3
        logger.info("Uploading the image file to S3 bucket");
        String imageUrl = s3Service.uploadFile(request.getImageFile(), request.getUtilityAccountNumber());
        // Save the URL in the MeterReading org.cmpe295.utilityaccount.entity
        meterReading.setImageURL(imageUrl);
        //Get the predicted Meter Reading
        logger.info("Calling the inference endpoint to get meter reading predction");
        MeterReading meterReadingResponse = getPredictedReadingValue(request.getImageFile());
        logger.info("Returned from the predict method");
        if(meterReadingResponse!=null){
            logger.info("The predicted reading value: "+meterReadingResponse.getReadingValue());
            meterReading.setReadingValue(meterReadingResponse.getReadingValue());
            meterReading.setMeterImageMetadata(meterReadingResponse.getMeterImageMetadata());
            meterReading.setStatus(METER_READING_ENTRY_STATUS.PENDING_CONFIRMATION);
        }
        else{
            meterReading.setStatus(METER_READING_ENTRY_STATUS.ERROR);
        }
        // Save the MeterReading org.cmpe295.utilityaccount.entity to the database
        MeterReading savedMeterReadingEntry = meterReadingRepository.save(meterReading);
        logger.info(String.valueOf(savedMeterReadingEntry));
        logger.info("Meter Reading entry saved");
        logger.info("Meter Reading Id"+savedMeterReadingEntry.getReadingId());
        //logger.info("Meter Reading Value"+savedMeterReadingEntry.getReadingValue());
        //logger.info("Meter Reading Coordinates"+savedMeterReadingEntry.getMeterImageMetadata().getXCoordinate());
        //logger.info("Meter Reading Coordinates"+savedMeterReadingEntry.getMeterImageMetadata().getYCoordinate());
        //logger.info("Meter Reading Coordinates"+savedMeterReadingEntry.getMeterImageMetadata().getWidth());
        ///logger.info("Meter Reading Coordinates"+savedMeterReadingEntry.getMeterImageMetadata().getHeight());
        return savedMeterReadingEntry.getReadingId();
    }

    private MeterReading getPredictedReadingValue(MultipartFile imageFile) throws IOException {
        logger.info("Entered the prediction method");
        //Use the URL from prediction endpoint here
        //String predictEndpoint = predictionUrl;
        logger.info(predictEndpoint);
        MeterReading meterReadingResponse = new MeterReading();
        // Create the RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Convert MultipartFile to Resource
            Resource fileResource = new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            };

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send HTTP POST request to FastAPI endpoint
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    predictEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Process response
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                System.out.println("Response from python-->" + responseBody);
                try {
                    // Parse JSON response
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(responseBody);

                    // Extract predicted text and coordinates from detections array
                    JsonNode detectionsNode = rootNode.get("detections");
                    if (detectionsNode != null && detectionsNode.isArray() && !detectionsNode.isEmpty()) {
                        JsonNode firstDetection = detectionsNode.get(0);
                        String predictedText = firstDetection.get("predicted_text").asText();

                        // Extract integer from predicted text
                        String valueString = predictedText.replaceAll("[^\\d]", "");
                        logger.info("Meter Reading is " + valueString);

                        // Extract coordinates
                        JsonNode coordinatesNode = firstDetection.get("coordinates");
                        int x1 = coordinatesNode.get("x1").asInt();
                        int y1 = coordinatesNode.get("y1").asInt();
                        int x2 = coordinatesNode.get("x2").asInt();
                        int y2 = coordinatesNode.get("y2").asInt();

                        // Create MeterImageMetadata object with coordinates
                        MeterImageMetadata meterImageMetadata = new MeterImageMetadata();
                        meterImageMetadata.setXCoordinate(x1);
                        meterImageMetadata.setYCoordinate(y1);
                        meterImageMetadata.setWidth(x2-x1);
                        meterImageMetadata.setHeight(y2-y1);

                        // Create MeterReading object
                        MeterReading meterReading = new MeterReading();
                        meterReading.setReadingValue(Integer.parseInt(valueString));
                        meterReading.setMeterImageMetadata(meterImageMetadata);

                        // Return MeterReading
                        return meterReading;
                    } else {
                        System.err.println("No detections found in the response.");
                    }
                } catch (JsonProcessingException e) {
                    // Handle JSON parsing error
                    System.err.println("Error parsing JSON response: " + e.getMessage());
                } catch (Exception e) {
                    // Handle other exceptions
                    System.err.println("Error processing response: " + e.getMessage());
                }
            } else {
                // Handle non-200 status code
                System.err.println("Error: " + responseEntity.getStatusCodeValue() + " " + ((HttpStatus) responseEntity.getStatusCode()).getReasonPhrase());
            }

            // Return null if unable to extract reading and coordinates
            return null;
        } catch(IOException e) {
            e.printStackTrace();
        }
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
