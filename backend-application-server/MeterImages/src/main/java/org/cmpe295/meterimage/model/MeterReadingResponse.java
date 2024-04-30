package org.cmpe295.meterimage.model;

import lombok.*;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MeterReadingResponse {
    private Long readingId;
    private String dateOfReading;
    private String dateOfBillGeneration;
    private METER_READING_ENTRY_STATUS status;
    private String imageURL;
    private Float billAmount;
    private Integer readingValue;
}
