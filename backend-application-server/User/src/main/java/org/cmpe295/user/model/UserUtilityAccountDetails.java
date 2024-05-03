package org.cmpe295.user.model;

import org.cmpe295.user.entity.UtilityAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserUtilityAccountDetails {
    UtilityAccount getUtilityAccount();
    LocalDateTime getDateOfLink();
}
