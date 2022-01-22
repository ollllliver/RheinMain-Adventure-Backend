package de.hsrm.mi.swt.rheinmainadventure.benutzer;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
/**
* Service für Operationen auf der Benutzerdatenbank
*/
public interface BenutzerService {
    boolean pruefeLogin (String loginname, String passwort);
    Benutzer registriereBenutzer(Benutzer neubenutzer);
    Benutzer findeBenutzer(String loginname);
}