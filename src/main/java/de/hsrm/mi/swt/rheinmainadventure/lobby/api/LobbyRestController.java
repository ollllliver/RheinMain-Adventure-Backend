package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest Controller für /abi/lobby/*
 * 
 * Alle REST aufrufe zum Thema Lobby kommen hier an, werden zum verarbeiten an
 * den LobbyService weitergeleitet und hier wieder als Antwort zurück gesendet.
 * 
 */
@RestController
@RequestMapping(value = {"/api/lobby/*"})
@SessionAttributes(names = {"loggedinBenutzername", "aktuelleLobby"})
public class LobbyRestController {
    // Hier ist die REST Schnittstelle fuer /api/lobby/... Jede REST Anfrage auf
    // diese Domain geht hierueber und wird in dieser Klasse bearbeitet.
    // Auf die Lobbyinstanzen kann ueber den LobbyService zugegriffen werden
    // (autowired).
    Logger logger = LoggerFactory.getLogger(LobbyRestController.class);

    @Autowired
    private LobbyService lobbyservice;

    /**
     * Erfragt eine Liste aller vorhandenen Lobbys.
     *
     * @return Liste von allen Lobbys.
     */
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
    public LobbyMessage lobbyBeitretenByID(@PathVariable String lobbyId, Model m) {
        logger.info("POST /api/lobby/join/" + lobbyId);
        if (m.getAttribute("aktuelleLobby").equals("") || m.getAttribute("aktuelleLobby").equals(lobbyId)) {
            LobbyMessage tempLobbyMessage = lobbyservice.joinLobbybyId(lobbyId,
                    m.getAttribute("loggedinBenutzername").toString());
            if (!tempLobbyMessage.getIstFehler()) {
                m.addAttribute("aktuelleLobby", tempLobbyMessage.getPayload());
            }
            return tempLobbyMessage;
        }
        return new LobbyMessage(NachrichtenCode.BEREITS_IN_ANDERER_LOBBY, true,
                m.getAttribute("aktuelleLobby").toString());
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
        logger.info("DELETE /api/lobby/leave/" + lobbyId);
        logger.info("USER " + m.getAttribute("loggedinBenutzername").toString() + " will die Lobby verlassen");
        if (!m.getAttribute("aktuelleLobby").equals("")) {
            m.addAttribute("aktuelleLobby", "");
        }
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
    public LobbyMessage neueLobbyErstellen(Model m) {
        // GET /api/lobby/neu - erstellen einer neuen Lobby ueber den LobbyService
        // zurueckgesendet wird die neu erstellte Lobbyinstanz, damit das Frontend auf
        // die Lobbyseite mit der im Backend erstellten LobbyID weiterleidten kann.
        logger.info("POST /api/lobby/neu  Von : " + m.getAttribute("loggedinBenutzername").toString());
        if (m.getAttribute("aktuelleLobby").equals("")) {
            String lobbyID = lobbyservice.lobbyErstellen(m.getAttribute("loggedinBenutzername").toString())
                    .getlobbyID();
            return new LobbyMessage(NachrichtenCode.NEUE_LOBBY, false, lobbyID);
        }
        return new LobbyMessage(NachrichtenCode.BEREITS_IN_ANDERER_LOBBY, true,
                m.getAttribute("aktuelleLobby").toString());
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
    public LobbyMessage lobbyBeitretenZufaellig(Model m) {
        return lobbyservice.lobbyBeitretenZufaellig(m.getAttribute("loggedinBenutzername").toString());
    }

    @PostMapping("/{lobbyId}/start")
    public LobbyMessage startGame(@PathVariable String lobbyId) {
        return lobbyservice.starteCountdown(lobbyId);
    }

    /**
     * Setzt bei richtiger Berechtigung das Spielerlimit der Loby neu.
     *
     * @param lobbyId      der zu ändernden Lobby
     * @param spielerlimit das neu gesetzt werden soll
     * @param m
     * @return LobbyMessage mit information über Erfolg/Misserfolg
     */
    @PatchMapping("/{lobbyId}/spielerlimit")
    public LobbyMessage patchSpielerlimit(@PathVariable String lobbyId, @RequestBody int spielerlimit, Model m) {
        return lobbyservice.setSpielerlimit(lobbyId, spielerlimit, m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * Setzt bei richtiger Berechtigung die Privatsphäre der Loby neu.
     *
     * @param lobbyId   der zu ändernden Lobby
     * @param istPrivat boolean, das neu gesetzt werden soll.
     * @param m
     * @return LobbyMessage mit information über Erfolg/Misserfolg
     */
    @PatchMapping("/{lobbyId}/privacy")
    public LobbyMessage patchPrivacy(@PathVariable String lobbyId, @RequestBody Boolean istPrivat, Model m) {
        return lobbyservice.setPrivacy(lobbyId, istPrivat, m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * Ändert bei richtiger Berechtigung den Host der Loby.
     *
     * @param lobbyId der zu ändernden Lobby
     * @param host    der neuer Host der Lobby werden soll.
     * @param m
     * @return LobbyMessage mit information über Erfolg/Misserfolg
     */
    @PatchMapping("/{lobbyId}/host")
    public LobbyMessage patchHost(@PathVariable String lobbyId, @RequestBody Spieler host, Model m) {
        return lobbyservice.setHost(lobbyId, host, m.getAttribute("loggedinBenutzername").toString());
    }

    /**
     * Wirft beu richtiger Berechtigung einen Mitspieler aus der Lobby.
     *
     * @param lobbyId             der zu ändernden Lobby
     * @param zuEntfernendSpieler der zu entfernende Mitspieler
     * @param m
     * @return LobbyMessage mit information über Erfolg/Misserfolg
     */
    @DeleteMapping("/{lobbyId}/teilnehmer")
    public LobbyMessage deleteTeilnehmer(@PathVariable String lobbyId, @RequestBody Spieler zuEntfernendSpieler,
            Model m) {
        return lobbyservice.removeSpieler(lobbyId, zuEntfernendSpieler,
                m.getAttribute("loggedinBenutzername").toString());
    }
}
