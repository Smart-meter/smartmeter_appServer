package org.cmpe295.utilityaccount.repository;

import org.cmpe295.utilityaccount.entity.UtilityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityAccountRepository extends JpaRepository<UtilityAccount, Long> {
    UtilityAccount findByStreetAndAptSuiteAndCityAndStateAndZipCodeAndCountry(String street, String aptSuite, String city, String state, String zipCode, String country);
    UtilityAccount findByUtilityAccountNumber(Long utilityAccountNumber);
}
