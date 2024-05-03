package org.cmpe295.meterimage.controller;

import org.cmpe295.meterimage.model.MeterReadingEntryUpdateRequest;
import org.cmpe295.meterimage.model.MeterReadingResponse;
import org.cmpe295.meterimage.service.MeterReadingService;
import org.cmpe295.user.entity.MeterImageMetadata;
import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meter-reading")
public class MeterReadingController {
    @Autowired
    private MeterReadingService meterReadingService;
    @GetMapping("/{readingId}")
    public ResponseEntity<MeterReadingResponse> getMeterReadingById(@PathVariable Long readingId) {
        MeterReadingResponse meterReading = meterReadingService.getMeterReadingById(readingId);
        if (meterReading != null) {
            return new ResponseEntity<>(meterReading, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{readingId}")
    public ResponseEntity<MeterReadingResponse> updateMeterReading(@PathVariable Long readingId, @RequestBody MeterReadingEntryUpdateRequest request) {
        MeterReading existingReading = meterReadingService.getMeterReadingEntryById(readingId);
        if (existingReading == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Update readingValue if provided in the request
        if (request.getReadingValue() != null) {
            existingReading.setReadingValue(request.getReadingValue());
            existingReading.setBillAmount((float) (existingReading.getReadingValue()*0.01));
            existingReading.setDateOfBillGeneration(LocalDateTime.now());
        }

        // Update meterImageMetadata if provided in the request
        if (request.getMeterImageMetadata() != null) {
            existingReading.setMeterImageMetadata(request.getMeterImageMetadata());
        }
        existingReading.setStatus(METER_READING_ENTRY_STATUS.MANUAL_ENTRY);

        // Save the updated reading
        MeterReadingResponse updatedReading = meterReadingService.updateMeterReading(existingReading);

        return new ResponseEntity<>(updatedReading, HttpStatus.OK);
    }
    @PutMapping("/discard/{readingId}")
    public ResponseEntity<Void> discardMeterReading(@PathVariable Long readingId) {
        MeterReading existingReading = meterReadingService.getMeterReadingEntryById(readingId);
        if (existingReading == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Update the status of the meter reading to "discarded"
        existingReading.setStatus(METER_READING_ENTRY_STATUS.DISCARDED);
        // Save the updated reading (if necessary, depending on your service implementation)
        MeterReadingResponse updatedReading = meterReadingService.updateMeterReading(existingReading);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/billpay/{readingId}")
    public ResponseEntity<Void> payForMeterReading(@PathVariable Long readingId) {
        MeterReading existingReading = meterReadingService.getMeterReadingEntryById(readingId);
        if (existingReading == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!(existingReading.getStatus() == METER_READING_ENTRY_STATUS.CONFIRMED || existingReading.getStatus() ==  METER_READING_ENTRY_STATUS.MANUAL_ENTRY)){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"The meter reading has to be confirmed to pay the bill");
        }
        existingReading.setStatus(METER_READING_ENTRY_STATUS.BILL_PAID);
        // Save the updated reading (if necessary, depending on your service implementation)
        MeterReadingResponse updatedReading = meterReadingService.updateMeterReading(existingReading);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/confirm/{readingId}")
    public ResponseEntity<Void> confirmMeterReading(@PathVariable Long readingId) {
        MeterReading existingReading = meterReadingService.getMeterReadingEntryById(readingId);
        if (existingReading == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Update the status of the meter reading to "discarded"
        existingReading.setStatus(METER_READING_ENTRY_STATUS.CONFIRMED);
        existingReading.setBillAmount((float) (existingReading.getReadingValue()*0.01));
        existingReading.setDateOfBillGeneration(LocalDateTime.now());
        // Save the updated reading (if necessary, depending on your service implementation)
        MeterReadingResponse updatedReading = meterReadingService.updateMeterReading(existingReading);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/utility-account/{utilityAccountNumber}")
    public ResponseEntity<List<MeterReadingResponse>> getMeterReadingsByUtilityAccount(@PathVariable Long utilityAccountNumber) {
        List<MeterReadingResponse> meterReadings = meterReadingService.getMeterReadingsByUtilityAccount(utilityAccountNumber);
        if (!meterReadings.isEmpty()) {
            return new ResponseEntity<>(meterReadings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/utility-account/latest/{utilityAccountNumber}")
    public ResponseEntity<MeterReadingResponse> getLatestMeterReadingsByUtilityAccount(@PathVariable Long utilityAccountNumber) {
        MeterReadingResponse meterReading = meterReadingService.getLatestMeterReadingByUtilityAccount(utilityAccountNumber);
        if (meterReading != null) {
            return new ResponseEntity<>(meterReading, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
