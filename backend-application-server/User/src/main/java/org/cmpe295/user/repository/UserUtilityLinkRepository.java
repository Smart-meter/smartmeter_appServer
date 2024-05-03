package org.cmpe295.user.repository;

import org.cmpe295.user.entity.UserUtilityLink;
import org.cmpe295.user.entity.UtilityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserUtilityLinkRepository extends JpaRepository<UserUtilityLink, Long> {
}
