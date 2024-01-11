package org.cmpe295.user.repository;

import org.cmpe295.user.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    Optional<MeterReading> findByUtilityAccountNumber(String utilityAccountNumber);
}
