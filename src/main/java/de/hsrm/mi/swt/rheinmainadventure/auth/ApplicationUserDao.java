package de.hsrm.mi.swt.rheinmainadventure.auth;

import java.util.Optional;

public interface ApplicationUserDao {

    public Optional<ApplicationUser> selectApplicationUserByUsername (String username);

}
