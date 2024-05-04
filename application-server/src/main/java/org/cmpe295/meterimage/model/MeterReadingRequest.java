package org.cmpe295.meterimage.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor

public class MeterReadingRequest {
    private Integer readingValue;
    private Long utilityAccountNumber;
    private MultipartFile imageFile;
}
