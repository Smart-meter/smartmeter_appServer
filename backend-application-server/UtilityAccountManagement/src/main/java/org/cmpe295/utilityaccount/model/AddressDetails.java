package org.cmpe295.utilityaccount.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDetails {
    private String street;
    private String aptSuite;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA"; // Default value
}
