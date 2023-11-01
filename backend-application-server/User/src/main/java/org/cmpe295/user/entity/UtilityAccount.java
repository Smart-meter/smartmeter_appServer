package org.cmpe295.user.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long utilityAccountNumber;
    private Long meterNumber;
    private String meterType;
    private Address address;
    @OneToMany(mappedBy="utilityAccount")
    private List<UserUtilityLink> userUtilityLinks;
    @OneToMany(mappedBy="utilityAccount")
    private List<MeterReading> meterReadings;
}
