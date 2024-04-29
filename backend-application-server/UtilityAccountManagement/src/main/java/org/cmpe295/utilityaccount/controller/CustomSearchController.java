package org.cmpe295.utilityaccount.controller;

import org.cmpe295.utilityaccount.entity.UtilityAccount;
import org.cmpe295.utilityaccount.model.AddressDetails;
import org.cmpe295.utilityaccount.repository.UtilityAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/utility-accounts")
public class CustomSearchController {
    @Autowired
    private UtilityAccountRepository utilityAccountRepository;
    @GetMapping("/searchByAddress")
    public ResponseEntity<?> searchUtilityAccount(@RequestBody AddressDetails addressDetails) {
        // Extract address details from the request body
        String street = addressDetails.getStreet();
        String aptSuite = addressDetails.getAptSuite();
        String city = addressDetails.getCity();
        String state = addressDetails.getState();
        String zipCode = addressDetails.getZipCode();
        String country = addressDetails.getCountry();


        UtilityAccount utilityAccount = utilityAccountRepository.findByStreetAndAptSuiteAndCityAndStateAndZipCodeAndCountry(street, aptSuite, city, state, zipCode, country);

        // If utility account found, return its number
        if (utilityAccount != null) {
            return ResponseEntity.ok(utilityAccount);
        } else {
            // If utility account not found, return an error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utility account not found for the provided address details.");
        }
    }
    @GetMapping("/searchByNumber/{utilityAccountNumber}")
    public ResponseEntity<?> searchUtilityAccountByNumber(@PathVariable Long utilityAccountNumber) {
        UtilityAccount utilityAccount = utilityAccountRepository.findByUtilityAccountNumber(utilityAccountNumber);

        if (utilityAccount != null) {
            return ResponseEntity.ok(utilityAccount);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utility account not found for the provided utility account number.");
        }
    }
}
