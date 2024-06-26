package org.cmpe295.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.cmpe295.user.entity.enums.METER_TYPE;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilityAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meterNumber;
    @Column(nullable = false)
    private Long utilityAccountNumber;

    private METER_TYPE meterType;
    private Address address;
    @JsonIgnore
    @OneToMany(mappedBy="utilityAccount")
    private List<UserUtilityLink> userUtilityLinks;
    @OneToMany(mappedBy="utilityAccount")
    @JsonIgnore
    private List<MeterReading> meterReadings;
}
