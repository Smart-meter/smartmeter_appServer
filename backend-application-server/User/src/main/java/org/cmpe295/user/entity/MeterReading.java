package org.cmpe295.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readingId;
    private LocalDate dateOfReading;
    private LocalDate dateOfBillGeneration;
    private METER_READING_ENTRY_STATUS status;
    private String imageURL;
    private Float billAmount;
    private Integer readingValue;
    private MeterImageMetadata meterImageMetadata;
    @ManyToOne
    @JoinColumn(name="utilityAccountNumber", nullable=false,referencedColumnName = "utilityAccountNumber")
    private UtilityAccount utilityAccount;
}
