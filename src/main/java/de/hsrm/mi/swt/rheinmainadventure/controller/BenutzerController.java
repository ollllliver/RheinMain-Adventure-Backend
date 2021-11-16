package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BenutzerController {

    @Autowired
    IntBenutzerRepo benutzerRepo;


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

    @GetMapping("/benutzer/{id}")
    public ResponseEntity<Benutzer> getByID(@PathVariable Long id){
       Optional<Benutzer> benutzer = benutzerRepo.findById(id);

       if (benutzer.isPresent()){
           return new ResponseEntity<Benutzer>(benutzer.get(), HttpStatus.OK);
       }

       return new ResponseEntity<Benutzer>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity<Benutzer> register(@RequestBody Benutzer benutzer) {
        try {
            return new ResponseEntity<>(benutzerRepo.save(benutzer), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/login")
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

    @PutMapping("/benutzer/{id}")
    public ResponseEntity<Benutzer> updateBenutzer(@RequestBody Benutzer benutzer){
        try {
            return new ResponseEntity<Benutzer>(benutzerRepo.save(benutzer), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
