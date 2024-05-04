package org.cmpe295.user.repository;

import org.cmpe295.user.entity.MeterReading;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.entity.enums.METER_READING_ENTRY_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    Optional<MeterReading> findFirstByUtilityAccountOrderByDateOfReadingDescReadingIdDesc(UtilityAccount utilityAccount);

    List<MeterReading> findByUtilityAccountUtilityAccountNumber(Long utilityAccountNumber);
    Optional<MeterReading> findFirstByUtilityAccountUtilityAccountNumberAndStatusNotOrderByDateOfReadingDesc(Long utilityAccountNumber, METER_READING_ENTRY_STATUS status);
}
