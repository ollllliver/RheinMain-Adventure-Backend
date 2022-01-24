package de.hsrm.mi.swt.rheinmainadventure.jwt;

import de.hsrm.mi.swt.rheinmainadventure.benutzer.BenutzerService;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import de.hsrm.mi.swt.rheinmainadventure.security.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

@SessionAttributes(names = {"loggedinBenutzername"})
@RestController
public class JwtLoginController {
    Logger logger = LoggerFactory.getLogger(JwtLoginController.class);

    /*
     * AuthenticationManager ermöglicht Login-Überprüfung gegen alle konfigurierten
     * Authentifikations-Quellen (siehe Security-Konfiguration). Man könnte auch den
     * eigenen UserDetailService verwenden, dann wären aber "nur" die Datenbank-User
     * abgedeckt, nicht z.B. die "in memory" angelegten
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    BenutzerService benutzerService;

    @Autowired
    IntBenutzerRepo benutzerRepo;

    //Session-Attribut wird gesetzt
    @ModelAttribute("loggedinBenutzername")
    public String merkeUser() {
        return new String();
    }


    /**
     * Get Anfrage, die prüft ob ein Nutzer bereits eingeloggt ist
     * @param m Model, in dem das Attribut mit eingeloggt Status gesetzt wird
     * @return gefunden Nutzer falls einer eingeloggt, sonst null
     */
    @GetMapping("/api/check")
    public ResponseEntity<Benutzer> testObEingeloggt(Model m){


        Benutzer check = null;
        try{
            check = benutzerService.findeBenutzer(m.getAttribute("loggedinBenutzername").toString());
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        if (check != null) {
            logger.info("Nutzer mit gesetzten Sessionattribut gefunden");
            return new ResponseEntity<Benutzer>(check, HttpStatus.OK);
        }
        else{
            logger.info("Session attribut leer, bisher kein Nutzer eingeloggt.");
            m.addAttribute("loggedinBenutzername", "");

            return new ResponseEntity<Benutzer>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Post Anfrage zum abmelden des Benutzers
     * @param benutzer liefert die übermittelten Daten aus dem Formular
     * @return registrierte Benutzer falls erfolgreich, sonst null
     */
    @PostMapping("/api/logout")
    public ResponseEntity<Benutzer> abmelden(Model m,@RequestBody de.hsrm.mi.swt.rheinmainadventure.jwt.JwtLoginRequest benutzer) {

        if ((benutzerRepo.findByBenutzername(benutzer.getBenutzername())) != null) {
            m.addAttribute("loggedinBenutzername", "");
            logger.info("Session attribut leer gesetzt -> Nutzer ausgeloggt");
            return new ResponseEntity<Benutzer>(benutzerRepo.findByBenutzername(benutzer.getBenutzername()), HttpStatus.ACCEPTED);
        }else {
            return new ResponseEntity<Benutzer>(HttpStatus.UNAUTHORIZED);
        }

        //        if (benutzerService.findeBenutzer(benutzer.getBenutzername()) ==null ) {
//            try {
//                logger.info("Nutzer wird registriert");
//                Benutzer registriert = benutzerService.registriereBenutzer(benutzer);
//                m.addAttribute("loggedinBenutzername", "");
//                return new ResponseEntity<Benutzer>(registriert, HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity<Benutzer>(HttpStatus.NO_CONTENT);
//            }
//        }
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /**
     * Post Anfrage zum Registrieren des Benutzers
     * @param benutzer liefert die übermittelten Daten aus dem Formular
     * @return registrierte Benutzer falls erfolgreich, sonst null
     */
    @PostMapping("/api/register")
    public ResponseEntity<Benutzer> registrieren(@RequestBody de.hsrm.mi.swt.rheinmainadventure.jwt.JwtLoginRequest benutzer) {
        if (benutzerService.findeBenutzer(benutzer.getBenutzername()) ==null ) {
            try {
                logger.info("Nutzer wird registriert");
                Benutzer registriert = benutzerService.registriereBenutzer(benutzer);
                return new ResponseEntity<Benutzer>(registriert, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<Benutzer>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /*
     * Test aus Kommandozeile z.B. mit folgendem Kommando möglich
     * curl -v -X POST -H "Content-Type: application/json" -d '{ "username":"joghurta", "password":"geheim123" }' http://localhost:8080/api/login
     */
    @PostMapping("/api/login")
    public ResponseEntity<String> get_jwt_token(Model m, @RequestBody de.hsrm.mi.swt.rheinmainadventure.jwt.JwtLoginRequest logindata) {
        String token=null;
        Authentication auth = null;
        try {
            // AuthenticationManager zur Überprüfung der übergebenen Login-Daten verwenden,
            // würde eine Authentication(Sub-)Exception werfen, falls das fehlschlüge
            auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(logindata.getBenutzername(), logindata.getPasswort()));
            logger.info("get_jwt_token({}) -> isAuthenticated {}", logindata, auth);

            // (Ersten) Authority-Eintrag ermitteln und daraus Rollen-Namen basteln
            var authority = auth.getAuthorities().stream().findFirst().orElseThrow();
            var rollenname = ((MyUserDetails) auth.getPrincipal()).getBenutzer().getRoles();
            logger.info("get_jwt_token({}) Authority {} -> rollenname {}", logindata, authority, rollenname);

            // JWT mit Claims zu Benutzernamen und Rolle basteln
            token = jwtUtil.bastelJwtToken(logindata.getBenutzername(), rollenname);

            // Modelattribute setzen
            m.addAttribute("loggedinBenutzername", logindata.getBenutzername());

            return new ResponseEntity<String>(token, HttpStatus.OK);

        } catch (AuthenticationException e) {
            // Authentifizierung fehlgeschlagen, HTTP-Status für Client auf 401 (unauthorized) setzen
            logger.error("get_jwt_token({}) ERROR, NOT isAuthenticated; {}", logindata, auth, e.getMessage());
          // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, logindata.getBenutzername());

            return new ResponseEntity<String>("Backend Login fehlgeschlagen", HttpStatus.UNAUTHORIZED);
        }
        // Authentifizierung erfolgreich - JWT als einfachen String an Client zurückgeben

    }
}
