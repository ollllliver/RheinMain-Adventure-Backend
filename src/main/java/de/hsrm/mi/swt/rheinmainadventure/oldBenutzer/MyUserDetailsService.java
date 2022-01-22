package de.hsrm.mi.swt.rheinmainadventure.oldBenutzer;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    IntBenutzerRepo userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Benutzer user = userRepository.findByBenutzername(username);
        logger.info("Benutzer " + user + " gefunden und eingeloggt.");
        if (user != null){
            return new MyUserDetails(user);
        }else {
            throw new UsernameNotFoundException("Benutzer nicht gefunden: " + username);
        }

    }
}
