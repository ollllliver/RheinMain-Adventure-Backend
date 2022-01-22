package de.hsrm.mi.swt.rheinmainadventure.oldBenutzer;

import de.hsrm.mi.swt.rheinmainadventure.controller.BenutzerController;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.oldJwt.JwtLoginRequest;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;


@Service
public class BenutzerServiceImpl implements BenutzerService {

    @Autowired
    private IntBenutzerRepo benutzerrepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(BenutzerController.class);

    /**
     * Mehode zum Prüfen des Logins
     * @param loginname übermittelter loginname
     * @param passwort übermitteltes passwort
     * @return true falls login erfolgreich, sonst false
     */
    @Override
    public boolean pruefeLogin(String loginname, String passwort) {
        if (findeBenutzer(loginname) != null){
            if(passwort.equals(findeBenutzer(loginname).getPasswort())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Methode zum registrieren in der Datenbank
     * @param neubenutzer übermittelter Nutzer der registriert werden soll
     * @return registrierten Nutzer falls erfolgreich, sonst null
     */
    @Transactional
    @Override
    public Benutzer registriereBenutzer(@RequestBody JwtLoginRequest neubenutzer) {
       logger.info(neubenutzer.toString());
        if(benutzerrepository.findByBenutzername(neubenutzer.getBenutzername()) == null) {

            Benutzer benutzer = new Benutzer();
            benutzer.setBenutzername(neubenutzer.getBenutzername());
            benutzer.setPasswort(passwordEncoder.encode(neubenutzer.getPasswort()));
            //benutzer.setRoles("BENUTZER");
            benutzer.setActive(true);

            Benutzer benutzer2222222 = entityManager.merge(benutzer);

            logger.info("benutezr mir id : " + benutzer2222222.getBenutzerId());

            return benutzer2222222;
        } else {
            return null;
        }
    }

    /**
     * Methode zum prüfen ob Nutzer in der Datenbank
     * @param loginname übermittelter nutzername des gesucht wird
     * @return Nutzer falls erfolgreich gefunden, sonst null
     */
    @Override
    public Benutzer findeBenutzer(String loginname) {
        Benutzer gefunden = benutzerrepository.findByBenutzername(loginname);
        return gefunden;
    }

}
