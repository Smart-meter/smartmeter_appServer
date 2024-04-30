package org.cmpe295.user.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String street; // e.g., 100 Main ST
    private String aptSuite;
    private String city;
    private String state;
    private String zip;
    private String country = "USA";
}
