package org.cmpe295.user.repository;

import org.cmpe295.user.entity.UtilityAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilityAccountRepository extends JpaRepository<UtilityAccount, Long> {
    Optional<UtilityAccount> findByUtilityAccountNumber(Long utilityAccountNumber);
}
