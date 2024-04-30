package org.cmpe295.user.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cmpe295.user.entity.enums.ROLE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private ROLE role;
    //Address details of the Utility Account
    private String street;
    private String aptSuite;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA"; // Default value
    private Long utilityAccountNumber;
}
