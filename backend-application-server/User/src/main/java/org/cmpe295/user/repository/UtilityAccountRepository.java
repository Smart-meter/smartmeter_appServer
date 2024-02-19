package org.cmpe295.user.repository;

import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.model.UserUtilityAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UtilityAccountRepository extends JpaRepository<UtilityAccount, Long> {
    Optional<UtilityAccount> findByUtilityAccountNumber(Long utilityAccountNumber);
    @Query("SELECT ul.utilityAccount AS utilityAccount, ul.dateOfLink AS dateOfLink FROM UserUtilityLink ul " +
            "WHERE ul.user.id = :userId AND ul.isActive = true " +
            "ORDER BY ul.dateOfLink DESC")
    Optional<UserUtilityAccountDetails> findFirstActiveUtilityAccountDetailsByUserId(@Param("userId") Long userId);
}


