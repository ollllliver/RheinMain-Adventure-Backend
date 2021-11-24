package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.entities.UpdateBenutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
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
    IntBenutzerRepo benutzerRepo;



    /**
     * Gibt alle Nutzer mit ID und Name in einer Liste aus
     * @return ResponseEntity vom Typ Liste von Benutzern
     */
    @GetMapping("/benutzer")
    public ResponseEntity<List<Benutzer>> getAll(){

        try {
            List<Benutzer> list = benutzerRepo.findAll();
            if (list.isEmpty() || list.size() == 0){
                return new ResponseEntity<List<Benutzer>>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<List<Benutzer>>(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gibt Nutzer mit spezifischer ID aus wenn dieser existiert
     * @param id
     * @return ResponseEntity vom Typ Benutzer
     */
    @GetMapping("/benutzer/{id}")
    public ResponseEntity<Benutzer> getByID(@PathVariable Long id){
       Optional<Benutzer> benutzer = benutzerRepo.findById(id);

       if (benutzer.isPresent()){
           return new ResponseEntity<Benutzer>(benutzer.get(), HttpStatus.OK);
       }

       return new ResponseEntity<Benutzer>(HttpStatus.NOT_FOUND);
    }

    /**
     * Nimmt Benutzer aus und versucht diesen in die Datenbank zu fuettern
     * @param benutzer
     * @return ResponseEntity HttpStatus CREATED bzw. CONFLICT wenn schon existiert
     */ 
    @PostMapping("/benutzer/register")
    public ResponseEntity<Benutzer> register(@RequestBody Benutzer benutzer) {
        // TODO: JSON Objekt aus Benutzer machen
        try {
            return new ResponseEntity<>(benutzerRepo.save(benutzer), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Nimmt Benutzer aus und schaut ob dieser existiert und eingeloggt ist, setzt diesen online wenn dies nicht der Fall ist
     * @param benutzer
     * @return ResponseEntity ACCEPTED bzw. UNAUTHORIZED
     */
    @PostMapping("/benutzer/login")
    public ResponseEntity<Benutzer> login(@RequestBody Benutzer benutzer) {
        try {
            if (benutzerRepo.findByBenutzername(benutzer.getBenutzername()).getPasswort().equals(benutzer.getPasswort())){
                Benutzer test = benutzerRepo.findByBenutzername(benutzer.getBenutzername());
                test.setOnline(true);
                benutzerRepo.save(test);
                return new ResponseEntity<Benutzer>(benutzerRepo.findByBenutzername(benutzer.getBenutzername()), HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<Benutzer>(benutzer, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Nimmt id aus Linkzugriff und JSON Updatebenutzer mit nur Nutzername und Passwort und updated passwort
     * @param id
     * @param benutzer
     * @return ResponseEntity OK bzw. INTERNAL_SERVER_ERROR
     */
    @PutMapping("/benutzer/{id}")
    public ResponseEntity<Benutzer> updateBenutzer(@PathVariable Long id, @RequestBody UpdateBenutzer benutzer){
        try {
            Benutzer benutzerInfo = benutzerRepo.getById(id);
            benutzerInfo.setPasswort(benutzer.getPasswort());
            benutzerRepo.getById(id).setPasswort(benutzer.getPasswort());
            return new ResponseEntity<Benutzer>(benutzerRepo.save(benutzerInfo), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Nimmt id aus Linkzugriff und loescht Benutzer mit dieser id
     * @param id
     * @return ResponseEntity NO_CONTENT (wurde geloescht) bzw. INTERNAL_SERVER_ERROR
     */
    @DeleteMapping("/benutzer/{id}")
    public ResponseEntity<HttpStatus> deleteBenutzer (@PathVariable Long id){
        try {
            Optional<Benutzer> benutzer = benutzerRepo.findById(id);
            if (benutzer.isPresent()){
                benutzerRepo.delete(benutzer.get());
            }
            return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
