package org.cmpe295.meterimage.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cmpe295.user.entity.MeterImageMetadata;
@Getter
@Setter
@NoArgsConstructor
public class MeterReadingEntryUpdateRequest {
    private Integer readingValue;
    private MeterImageMetadata meterImageMetadata;
}
