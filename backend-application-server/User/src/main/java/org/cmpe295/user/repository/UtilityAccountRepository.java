package org.cmpe295.user.repository;

import org.cmpe295.user.entity.User;
import org.cmpe295.user.entity.UserUtilityLink;
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
    @Query("SELECT COUNT(uul) > 0 FROM UserUtilityLink uul WHERE uul.utilityAccount = :utilityAccount AND uul.isActive = true")
    boolean hasActiveUserUtilityLinks(@Param("utilityAccount") UtilityAccount utilityAccount);

    // Method to find the active utilityAccount by userName

    @Query("SELECT uul FROM UserUtilityLink uul WHERE uul.user.email = :userName AND uul.isActive = true")
    Optional<UserUtilityLink> findActiveUtilityAccountByUserName(@Param("userName") String userName);

    // Method to find the currently active User by utilityAccountNumber
    @Query("SELECT uul.user FROM UserUtilityLink uul WHERE uul.utilityAccount.utilityAccountNumber = :utilityAccountNumber AND uul.isActive = true")
    Optional<User> findActiveUserByUtilityAccountNumber(@Param("utilityAccountNumber") Long utilityAccountNumber);


}


