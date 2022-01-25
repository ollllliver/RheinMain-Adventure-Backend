package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.benutzer.BenutzerService;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api")
@SessionAttributes(names = {"loggedinBenutzername", "aktuelleLobby"})
@RestController
public class BenutzerController {

    public static final String LOGGEDIN_BENUTZERNAME = "loggedinBenutzername";
    @Autowired
    IntBenutzerRepo benutzerRepo;

    @Autowired
    BenutzerService benutzerService;
    Logger logger = LoggerFactory.getLogger(BenutzerController.class);

    //Session-Attribut wird gesetzt
    @ModelAttribute("loggedinBenutzername")
    public String merkeUser() {
        return "";
    }

    //Session-Attribut wird gesetzt
    @ModelAttribute("aktuelleLobby")
    public String merkeLobby() {
        return "";
    }

    /**
     * Get Anfrage auf die Benutzerroute
     *
     * @return liefert eine Liste mit allen Nutzern aus der Datenbank falls erfolgreich, sonst Fehler
     */
    @GetMapping("/benutzer")
    public ResponseEntity<List<Benutzer>> alleBenutzer() {
        try {
            List<Benutzer> list = benutzerRepo.findAll();
            if (list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.info("Nutzer gefunden");
            return new ResponseEntity<>(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get Anfrage, die prüft ob ein Nutzer bereits eingeloggt ist
     *
     * @param m                    Model, in dem das Attribut mit eingeloggt Status gesetzt wird
     * @param loggedinbenutzername ist der im Model zwingend enthaltene Benutzername
     * @return gefunden Nutzer falls einer eingeloggt, sonst null
     */
    @GetMapping("/benutzer/check")
    public ResponseEntity<Benutzer> testObEingeloggt(Model m, @ModelAttribute(LOGGEDIN_BENUTZERNAME) String loggedinbenutzername) {
        Benutzer check = benutzerService.findeBenutzer(loggedinbenutzername);
        if (check != null) {
            logger.info("Nutzer mit gesetzten Sessionattribut gefunden");
            return new ResponseEntity<>(check, HttpStatus.OK);
        } else {
            logger.info("Session attribut leer gesetzt");
            m.addAttribute(LOGGEDIN_BENUTZERNAME, "");

            return new ResponseEntity<>(check, HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Post Anfrage zum Registrieren des Benutzers
     *
     * @param benutzer liefert die übermittelten Daten aus dem Formular
     * @return registrierte Benutzer falls erfolgreich, sonst null
     */
    @PostMapping("/benutzer/register")
    public ResponseEntity<Benutzer> registrieren(@RequestBody Benutzer benutzer) {
        if (benutzerService.findeBenutzer(benutzer.getBenutzername()) == null) {
            try {
                logger.info("Nutzer wird registriert");
                benutzerService.registriereBenutzer(benutzer);
                return new ResponseEntity<>(benutzer, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(benutzer, HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Post Anfrage zum Einloggen eines Nutzers
     *
     * @param m        Model in dem das Attribut eingeloggt gesetzt wird
     * @param benutzer übermittelte Nutzerdaten
     * @return eingeloggten Nutzer falls erfolgreich, sonst Fehler
     */
    @PostMapping("/benutzer/login")
    public ResponseEntity<Benutzer> login(Model m, @RequestBody BenutzerPOJO benutzer) {
        try {
            if (benutzerService.pruefeLogin(benutzer.getBenutzername(), benutzer.getPasswort())) {
                m.addAttribute(LOGGEDIN_BENUTZERNAME, benutzer.getBenutzername());
                m.addAttribute("aktuelleLobby", "");
                logger.info("Session attribut gesetzt -> Nutzer eingeloggt");
                return new ResponseEntity<>(benutzerRepo.findByBenutzername(benutzer.getBenutzername()), HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Post Anfrage zum Ausloggen des Nutzers
     *
     * @param m        Model in dem das Attribut mit eingeloggt Status entfernt wird
     * @param benutzer übermittelte Daten des Nutzers der ausgeloggt werden soll
     * @return ausgeloggten Nutzer falls erfolgreich, sonst Fehler
     */
    @PostMapping("/benutzer/logout")
    public ResponseEntity<Benutzer> logout(Model m, @RequestBody BenutzerPOJO benutzer) {
        if ((benutzerRepo.findByBenutzername(benutzer.getBenutzername())) != null) {
            m.addAttribute(LOGGEDIN_BENUTZERNAME, "");
            m.addAttribute("aktuelleLobby", "");
            logger.info("Session attribut leer gesetzt -> Nutzer ausgeloggt");
            return new ResponseEntity<>(benutzerRepo.findByBenutzername(benutzer.getBenutzername()), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * @param benutzername benutzername von dem die Level angefragt werden
     * @return alle vom Nutzer erstellten Level in einer List
     */
    @GetMapping(value = "/benutzer/level/{benutzername}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Level> getLevelVonBenutzername(@PathVariable String benutzername) {
        Benutzer angefragerNutzer = benutzerService.findeBenutzer(benutzername);
        if (angefragerNutzer != null) {
            return angefragerNutzer.getErstellteLevel();
        } else {
            throw new BenutzerNichtGefundenException(benutzername + " nicht gefunden");
        }
    }
}
