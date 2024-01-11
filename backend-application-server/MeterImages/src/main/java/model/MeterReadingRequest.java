package model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class MeterReadingRequest {
    private Integer readingValue;
    private Long utilityAccountNumber;
    private MultipartFile imageFile;
}
