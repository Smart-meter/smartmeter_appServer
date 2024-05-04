package org.cmpe295.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Long currentUtilityAccountNumber;
    private String dateOfLink;
    private String dateOfReading;
    private String readingValue;
    private Long readingId;
}
