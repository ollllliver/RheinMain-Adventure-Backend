package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.BenutzerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class BenutzerController {

    @Autowired
    BenutzerRepo benutzerRepo;


    /**
     * Gibt alle Nutzer mit Benutzeramen in einer Liste aus
     *
     * @return ResponseEntity vom Typ Liste von Benutzern
     */
    @GetMapping("/benutzer")
    public ResponseEntity<List<Benutzer>> getAll() {

        try {
            List<Benutzer> list = benutzerRepo.findAll();
            if (list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gibt an, ob ein Nutzer mit einem spezifischen Benutzernamen existiert
     *
     * @param benutzername ist der benutzername, der mit der Db abgeglichen werden soll
     * @return ResponseEntity vom Typ Benutzer
     */
    @GetMapping("/benutzer/{benutzername}")
    public ResponseEntity<Benutzer> getByBenutzername(@PathVariable String benutzername) {
        Optional<Benutzer> benutzer = Optional.ofNullable(benutzerRepo.findByBenutzername(benutzername));

        if (benutzer.isPresent()) {
            return new ResponseEntity<>(benutzer.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Nimmt Benutzer aus dem request-Body und versucht diesen in die Datenbank zu fuettern
     *
     * @param benutzer ist der benutzer aus dem request-Body
     * @return ResponseEntity HttpStatus CREATED bzw. CONFLICT wenn schon existiert
     */
    @PostMapping("/benutzer/registrieren")
    public ResponseEntity<Benutzer> registrieren(@RequestBody Benutzer benutzer) {
        try {
            return new ResponseEntity<>(benutzerRepo.save(benutzer), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Nimmt Benutzer aus und schaut ob dieser existiert und das richtige Passwort angegeben hat
     *
     * @param benutzer ist der benutzer, der angemeldet werden soll
     * @return ResponseEntity ACCEPTED bzw. UNAUTHORIZED
     */
    @PostMapping("/benutzer/anmelden")
    public ResponseEntity<Benutzer> anmelden(@RequestBody Benutzer benutzer) {
        try {
            if (benutzerRepo.findByBenutzername(benutzer.getBenutzername()).getPasswort().equals(benutzer.getPasswort())) {
                return new ResponseEntity<>(benutzerRepo.findByBenutzername(benutzer.getBenutzername()), HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity<>(benutzer, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
