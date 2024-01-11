package org.cmpe295.user.repository;

import org.cmpe295.user.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

}
