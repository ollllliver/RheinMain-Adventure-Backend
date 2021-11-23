package de.hsrm.mi.swt.rheinmainadventure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.services.LobbyService;

@Controller
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;
    /*
     * Pseudo BenutzerService zum accessen der Datenbank um nutzernamen abzugleichen
     * und abzufragen
     * 
     * @Autowired private BenutzerService benutzerService;
     */

    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    // LobbyService injected and used as instance
    public LobbyController() {
    }

    @MessageMapping("/topic/lobby/create")
    @SendTo("/topic/lobby/create")
    public String createLobby(String msg) throws Exception {
        lg.info(msg);
        // Lobby Initialisieren und dem LobbyService in der LobbyListe hinzufügen
        lobbyService.createLobby();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    // Initialisiert das Starten einer Lobby also von Lobby -> Interner Spielwechsel
    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/topic/lobby/started")
    public int startGame(@PathVariable String lobbyId) throws Exception {
        int lobbyStartCountdown = 10;

        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        currLobby.setIstGestartet(true);
        return lobbyStartCountdown;
    }

    // Unter der nutzung eines Lobby-Links wird ein nutzer der jeweiligen Lobby
    // zugewiesen.
    @GetMapping("/lobby/{lobbyId}")
    public void lobbieBeitretenMitLink(@PathVariable String lobbyId, @SessionAttribute("username") String username) {
        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        if (!currLobby.getIstGestartet() && !currLobby.getIstVoll()) {
            /*
             * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
             * Benutzernamen findet. Benutzer tempNutzer =
             * benutzerService.getBenutzerByUsername(username);
             */

            Benutzer testNutzer = new Benutzer("jerry", "testy");

            // Unter der Kondition dass das Spielerlimit noch nicht erreicht wurde, wird ein
            // neuer Spieler hinzugefügt.
            currLobby.nutzerHinzufuegen(testNutzer);
        }
    }

    // Post Mapping auf die seite wo sich der Random-Join Button befindet.
    @PostMapping("/lobby")
    public void lobbieBeitretenZufaellig(@SessionAttribute("username") String username) {

        Lobby tempLobby = null;
        for (Lobby currLobby : lobbyService.getLobbies()) {
            if (!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()) {
                tempLobby = currLobby;
                break;
            }
        }
        /*
         * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
         * Benutzernamen findet. Benutzer tempNutzer =
         * benutzerService.getBenutzerByUsername(username);
         */
        Benutzer testNutzer = new Benutzer("jerry", "testy");
        if (tempLobby != null) {
            tempLobby.nutzerHinzufuegen(testNutzer);
        } else {
            lg.info("Ungueltige Lobby ID wurde angegeben.");
            // TODO : Redirect zur Homepage und Fehlermeldung Popup mit dem Logger Infotext.
        }
    }

}
