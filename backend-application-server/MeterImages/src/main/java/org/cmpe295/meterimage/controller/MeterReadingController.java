package org.cmpe295.meterimage.controller;

import org.cmpe295.meterimage.model.MeterReadingResponse;
import org.cmpe295.meterimage.service.MeterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
