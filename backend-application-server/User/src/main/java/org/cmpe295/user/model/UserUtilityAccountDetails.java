package org.cmpe295.user.model;

import org.cmpe295.user.entity.UtilityAccount;

import java.time.LocalDate;

public interface UserUtilityAccountDetails {
    UtilityAccount getUtilityAccount();
    LocalDate getDateOfLink();
}
