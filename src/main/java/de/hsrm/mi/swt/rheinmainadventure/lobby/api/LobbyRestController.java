package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;

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
public class LobbyRestController {
    // Hier ist die REST Schnittstelle fuer /api/lobby/... Jede REST Anfrage auf
    // diese Domain geht hierueber und wird in dieser Klasse bearbeitet.
    // Auf die Lobbyinstanzen kann ueber den LobbyService zugegriffen werden
    // (autowired).
    Logger logger = LoggerFactory.getLogger(LobbyRestController.class);

    @Autowired
    LobbyService lobbyservice;

    @GetMapping("alle")
    public List<Lobby> getAlleLobbies() {
        // GET /api/lobby/alle - liefert alle Lobbys.
        logger.info("GET /api/lobby/alle");
        return lobbyservice.getLobbies();
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
    public LobbyMessage lobbieBeitretenByID(@PathVariable String lobbyId, Model m) {
        logger.info("POST /api/lobby/join/" + lobbyId);
        return lobbyservice.joinLobbybyId(lobbyId, m.getAttribute("loggedinBenutzername").toString());
    }

    @DeleteMapping(value = "/leave/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage verlasseLobby(@PathVariable String lobbyId, Model m) {
        logger.info("POST /api/lobby/" + lobbyId + "/leave/");
        logger.info("USER " + m.getAttribute("loggedinBenutzername").toString() + " will die Lobby verlassen");
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
        logger.info("POST /api/lobby/neu  Von : "+m.getAttribute("loggedinBenutzername").toString());
        Lobby lobby = lobbyservice.lobbyErstellen(m.getAttribute("loggedinBenutzername").toString());
        return lobby;
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
        logger.info("GET /api/lobby/" + id);
        return lobbyservice.getLobbyById(id);
    }

    // @Chand work in progress

    @PostMapping(value = "/joinRandom", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage lobbieBeitretenZufaellig(Model m) {
        return lobbyservice.lobbieBeitretenZufaellig(m.getAttribute("loggedinBenutzername").toString());
    }

    @PostMapping("/{lobbyId}/start")
    public LobbyMessage startGame(@PathVariable String lobbyId) {
        return lobbyservice.starteCountdown(lobbyId);
    }

}
