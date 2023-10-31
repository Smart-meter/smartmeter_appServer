package org.cmpe295.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    private String street; // e.g., 100 Main ST
    private String city;
    private String state;
    private String zip;
}
