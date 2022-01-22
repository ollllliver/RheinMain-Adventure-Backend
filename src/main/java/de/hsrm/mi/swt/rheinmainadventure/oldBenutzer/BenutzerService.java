package de.hsrm.mi.swt.rheinmainadventure.oldBenutzer;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.oldJwt.JwtLoginRequest;

/**
* Service für Operationen auf der Benutzerdatenbank
*/
public interface BenutzerService {
    boolean pruefeLogin (String loginname, String passwort);
    Benutzer registriereBenutzer(JwtLoginRequest neubenutzer);
    Benutzer findeBenutzer(String loginname);
}