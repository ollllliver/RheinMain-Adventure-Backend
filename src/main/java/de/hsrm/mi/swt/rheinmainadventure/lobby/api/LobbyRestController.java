package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

/**
 * Rest Controller für /abi/lobby/*
 * 
 * Alle REST aufrufe zum Thema Lobby kommen hier an, werden zum verarbeiten an
 * den LobbyService weitergeleitet und hier wieder als Antwort zurück gesendet.
 * 
 * Es gibt: GET - api/lobby/alle POST - api/lobby/join{id} POST - api/lobby/neu
 * GET - api/lobby/{id}
 * 
 */
@RestController
@RequestMapping(value = { "/api/lobby/*" })
@SessionAttributes(names = { "loggedinBenutzername" })
@CrossOrigin("http://localhost:3000/")
public class LobbyRestController {
    // Hier ist die REST Schnittstelle fuer /api/lobby/... Jede REST Anfrage auf
    // diese Domain geht hierueber und wird in dieser Klasse bearbeitet.
    // Auf die Lobbyinstanzen kann ueber den LobbyService zugegriffen werden
    // (autowired).
    Logger logger = LoggerFactory.getLogger(LobbyRestController.class);

    @Autowired
    LobbyService lobbyservice;

    @GetMapping("alle")
    public List<Lobby> getAlleLobbys() {
        // GET /api/lobby/alle - liefert alle Lobbys.
        logger.info("GET /api/lobby/alle");
        return lobbyservice.getLobbys();
    }

    /**
     * api/lobby/join/{id} stößt beim lobbyservice das hinzufügen des Sessionscope
     * Users (später des Prinzipal Users) in die Lobby mit der mitgegebene ID an.
     * 
     * @param lobbyId, zu der der eingeloggte User hinzugefügt werden soll.
     * @param m        später Prinzipal prinz (eingeloggter User)
     * @return LobbyMessage mit Nachrichtencode
     * 
     */
    @PostMapping(value = "/join/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage lobbyBeitretenByID(@PathVariable String lobbyId, Model m, Principal principal) {
        logger.info("Principal: " + principal);
        logger.info(String.format("POST /api/lobby/join/%s", lobbyId));
        return lobbyservice.joinLobbybyId(lobbyId, m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * 
     * api/leave/{lobbyId} stoeßt beim lobbyservice das Verlassen an
     * 
     * @param lobbyId, die der SPieler verlassen will
     * @param m        eingeloggter User
     * @return LobbyMessage mit Nachrichtencode
     */

    @DeleteMapping(value = "/leave/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage verlasseLobby(@PathVariable String lobbyId, Model m) {
        logger.info(String.format("DELETE /api/lobby/leave/%s", lobbyId));
        logger.info(
                String.format("USER %s will die Lobby verlassen", m.getAttribute("loggedinBenutzername").toString()));
        return lobbyservice.spielerVerlaesstLobby(lobbyId, m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * api/lobby/neu stößt beim lobbyservice das erstellen einer neuen Lobby an und
     * gibt diese zurück.
     * 
     * @param m später Prinzipal prinz (eingeloggter User)
     * @return neu erstellte Lobby
     */
    @PostMapping(value = "neu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Lobby neueLobbyErstellen(Model m) {
        // GET /api/lobby/neu - erstellen einer neuen Lobby ueber den LobbyService
        // zurueckgesendet wird die neu erstellte Lobbyinstanz, damit das Frontend auf
        // die Lobbyseite mit der im Backend erstellten LobbyID weiterleidten kann.
        logger.info(String.format("POST /api/lobby/neu  Von : %s", m.getAttribute("loggedinBenutzername").toString()));
        return lobbyservice.lobbyErstellen(m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * Anfrage nach der Lobby mit mitgegebener ID.
     * 
     * @param id der gesuchten Lobby
     * @return angefragte Lobby
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Lobby getLobbyById(@PathVariable String id) {
        // GET /api/lobby/{id} - gibt die Lobby ueber die ID zurueck
        logger.info(String.format("GET /api/lobby/%s", id));
        return lobbyservice.getLobbyById(id);
    }

    // @Chand work in progress

    @PostMapping(value = "/joinRandom", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage lobbyBeitretenZufaellig(Model m) {
        return lobbyservice.lobbyBeitretenZufaellig(m.getAttribute("loggedinBenutzername").toString());
    }

    @PostMapping("/{lobbyId}/start")
    public LobbyMessage startGame(@PathVariable String lobbyId) {
        return lobbyservice.starteCountdown(lobbyId);
    }

    @PatchMapping("/{lobbyId}/spielerlimit")
    public LobbyMessage patchSpielerlimit(@PathVariable String lobbyId, @RequestBody int spielerlimit, Model m) {
        return lobbyservice.setSpielerlimit(lobbyId, spielerlimit, m.getAttribute("loggedinBenutzername").toString());
    }

    @PatchMapping("/{lobbyId}/privacy")
    public LobbyMessage patchPrivacy(@PathVariable String lobbyId, @RequestBody Boolean istPrivat, Model m) {
        return lobbyservice.setPrivacy(lobbyId, istPrivat, m.getAttribute("loggedinBenutzername").toString());
    }

    @PatchMapping("/{lobbyId}/host")
    public LobbyMessage patchHost(@PathVariable String lobbyId, @RequestBody Spieler host, Model m) {
        return lobbyservice.setHost(lobbyId, host, m.getAttribute("loggedinBenutzername").toString());
    }
}
