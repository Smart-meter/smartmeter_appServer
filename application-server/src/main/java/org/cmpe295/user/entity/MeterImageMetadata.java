package org.cmpe295.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class MeterImageMetadata {
    private int xCoordinate;
    private int yCoordinate;
    private int width;
    private int height;

}
