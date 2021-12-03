package de.hsrm.mi.swt.rheinmainadventure.benutzer;

import de.hsrm.mi.swt.rheinmainadventure.controller.BenutzerController;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BenutzerServiceImpl implements BenutzerService {

    @Autowired
    IntBenutzerRepo benutzerrepository;

    Logger logger = LoggerFactory.getLogger(BenutzerController.class);

    /**
     * Mehode zum Prüfen des Logins
     * @param loginname übermittelter loginname
     * @param passwort übermitteltes passwort
     * @return true falls login erfolgreich, sonst false
     */
    @Override
    public boolean pruefeLogin(String loginname, String passwort) {
        String pw = findeBenutzer(loginname).getPasswort();
        if(passwort.equals(pw)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Methode zum registrieren in der Datenbank
     * @param neubenutzer übermittelter Nutzer der registriert werden soll
     * @return registrierten Nutzer falls erfolgreich, sonst null
     */
    @Transactional
    @Override
    public Benutzer registriereBenutzer(Benutzer neubenutzer) {
       logger.info(neubenutzer.toString());
        if(benutzerrepository.findByBenutzername(neubenutzer.getBenutzername()) == null) {
            Benutzer gespeichert = benutzerrepository.save(neubenutzer);
            return gespeichert;
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
