package org.cmpe295.user.repository;

import org.cmpe295.user.entity.UtilityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UtilityAccountRepository extends JpaRepository<UtilityAccount, Long> {
    Optional<UtilityAccount> findByUtilityAccountNumber(Long utilityAccountNumber);
}
