package de.hsrm.mi.swt.rheinmainadventure.auth;

import com.google.common.collect.Lists;
import de.hsrm.mi.swt.rheinmainadventure.auth.ApplicationUser;
import de.hsrm.mi.swt.rheinmainadventure.auth.ApplicationUserDao;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserRole.ADMIN;
import static de.hsrm.mi.swt.rheinmainadventure.security.ApplicationUserRole.BENUTZER;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    IntBenutzerRepo benutzerRepo;

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

//    @Override
//    public Optional<ApplicationUser> registerApplicationUser(ApplicationUser applicationUser){
//        return
//    }

    private List<ApplicationUser> getApplicationUsers(){




        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(
                        "tester",
                        passwordEncoder.encode("tester"),
                        BENUTZER.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true

                ),
                new ApplicationUser(
                        "admin2",
                        passwordEncoder.encode("admin2"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );

        return applicationUsers;
    }
}
