package org.cmpe295.utilityaccount.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UtilityAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String aptSuite;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA"; // Default value
    private Long utilityAccountNumber;

    private LocalDate creationDate;
}
