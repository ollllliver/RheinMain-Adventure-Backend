package de.hsrm.mi.swt.rheinmainadventure.benutzer;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;

import java.util.Optional;

/**
* Service für Operationen auf der Benutzerdatenbank
*/
public interface BenutzerService {
    boolean pruefeLogin (String loginname, String passwort);
    Benutzer registriereBenutzer(de.hsrm.mi.swt.rheinmainadventure.jwt.JwtLoginRequest neubenutzer);
    Benutzer findeBenutzer(String loginname);
}