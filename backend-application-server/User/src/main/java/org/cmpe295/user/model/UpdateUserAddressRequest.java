package org.cmpe295.user.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAddressRequest {
    private String street;
    private String aptSuite;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA"; // Default value
    private Long utilityAccountNumber;
}
