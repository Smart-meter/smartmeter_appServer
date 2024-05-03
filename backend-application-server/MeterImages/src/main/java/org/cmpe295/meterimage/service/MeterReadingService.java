package org.cmpe295.meterimage.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.cmpe295.meterimage.model.MeterReadingResponse;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;
import org.cmpe295.user.repository.MeterReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class MeterReadingService {
    private final MeterReadingRepository meterReadingRepository;
    public MeterReadingResponse getMeterReadingById(Long readingId) {
        Optional<MeterReading> meterReadingOptional = meterReadingRepository.findById(readingId);

        if (meterReadingOptional.isPresent()) {
            // Map the entity to a DTO and return it
            return mapToDto(meterReadingOptional.get());
        }
        return null;
    }

    public List<MeterReadingResponse> getMeterReadingsByUtilityAccount(Long utilityAccountNumber) {
        // Retrieve the meter readings for the given utility account number
        List<MeterReading> meterReadings = meterReadingRepository.findByUtilityAccountUtilityAccountNumber(utilityAccountNumber);

        // Map the entities to DTOs
        List<MeterReadingResponse> meterReadingDtos = mapToDtoList(meterReadings);

        return meterReadingDtos;
    }
    // Utility method to map list of MeterReading entities to list of MeterReadingDto
    private List<MeterReadingResponse> mapToDtoList(List<MeterReading> meterReadings) {
        // Implement the mapping logic for each entity
        // This is a simplified example, you may need to adjust based on your actual entities and DTOs
        return meterReadings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Utility method to map MeterReading entity to MeterReadingDto
    private MeterReadingResponse mapToDto(MeterReading meterReading) {
        MeterReadingResponse meterReadingResponse = MeterReadingResponse.builder()
                .readingId(meterReading.getReadingId())
                .readingValue(meterReading.getReadingValue())
                .dateOfReading(String.valueOf(meterReading.getDateOfReading().toLocalDate()))
                .dateOfBillGeneration(String.valueOf(meterReading.getDateOfBillGeneration()!=null?meterReading.getDateOfBillGeneration().toLocalDate():"Not available"))
                .billAmount(meterReading.getBillAmount())
                .status(meterReading.getStatus())
                .imageURL(meterReading.getImageURL())
                .build();
        return meterReadingResponse;
    }

    public MeterReadingResponse getLatestMeterReadingByUtilityAccount(Long utilityAccountNumber) {
        // Retrieve the latest meter reading for the given utility account number
        Optional<MeterReading> optionalMeterReading = meterReadingRepository.findFirstByUtilityAccountUtilityAccountNumberAndStatusNotOrderByDateOfReadingDesc(utilityAccountNumber, METER_READING_ENTRY_STATUS.DISCARDED);

        // Check if a meter reading was found
        if (optionalMeterReading.isPresent()) {
            // Map the meter reading entity to a response DTO
            MeterReadingResponse meterReadingResponse = mapToDto(optionalMeterReading.get());
            return meterReadingResponse;
        } else {
            // If no meter reading is found, return null or throw an exception based on your requirement
            return null;
        }
    }

    public MeterReading getMeterReadingEntryById(Long readingId) {
        return meterReadingRepository.getReferenceById(readingId);
    }

    public MeterReadingResponse updateMeterReading(MeterReading existingReading) {
        MeterReading updatedReading =  meterReadingRepository.save(existingReading);
        return mapToDto(updatedReading);
    }
}
