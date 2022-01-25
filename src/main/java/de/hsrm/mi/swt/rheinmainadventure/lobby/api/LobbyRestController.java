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
 * <p>
 * Alle REST aufrufe zum Thema Lobby kommen hier an, werden zum verarbeiten an
 * den LobbyService weitergeleitet und hier wieder als Antwort zurück gesendet.
 */
@RestController
@RequestMapping(value = {"/api/lobby/*"})
@SessionAttributes(names = {"loggedinBenutzername", "aktuelleLobby"})
public class LobbyRestController {
    private static final String LOGGEDINBENUTZERNAME = "loggedinBenutzername";
    private static final String AKTUELLELOBBY = "aktuelleLobby";
    private static final LobbyMessage nichtEingeloggtLobbyFehlerMessage = new LobbyMessage(NachrichtenCode.NICHT_EINGELOGGT, true);
    // Hier ist die REST Schnittstelle fuer /api/lobby/... Jede REST Anfrage auf
    // diese Domain geht hierueber und wird in dieser Klasse bearbeitet.
    // Auf die Lobbyinstanzen kann ueber den LobbyService zugegriffen werden
    // (autowired).
    Logger logger = LoggerFactory.getLogger(LobbyRestController.class);
    private boolean modelAttributeGefunden = false;
    private Object attributeObject = null;


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
     */
    @PostMapping(value = "/join/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage lobbyBeitretenByID(@PathVariable String lobbyId, @ModelAttribute(AKTUELLELOBBY) String aktuelleLobby, Model m) {

        if (aktuelleLobby.equals("") || aktuelleLobby.equals(lobbyId)) {
            modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
            attributeObject = null;
            if (modelAttributeGefunden) {
                try {
                    attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                    if (attributeObject != null) {
                        logger.info("POST /api/lobby/join/{}", attributeObject);
                        LobbyMessage tempLobbyMessage = lobbyservice.joinLobbybyId(lobbyId, attributeObject.toString());
                        if (!tempLobbyMessage.getIstFehler()) {
                            m.addAttribute(AKTUELLELOBBY, tempLobbyMessage.getPayload());
                        }
                        return tempLobbyMessage;
                    }
                } catch (NullPointerException e) {
                    logger.error(String.valueOf(e));
                }
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
    }

    /**
     * api/leave/{lobbyId} stoeßt beim lobbyservice das Verlassen an
     *
     * @param lobbyId, die der SPieler verlassen will
     * @param m        eingeloggter User
     * @return LobbyMessage mit Nachrichtencode
     */
    @DeleteMapping(value = "/leave/{lobbyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage verlasseLobby(@PathVariable String lobbyId, Model m, @ModelAttribute(AKTUELLELOBBY) String aktuelleLobby) {
        logger.info("DELETE /api/lobby/leave/{}", attributeObject);
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    logger.info("USER {} will die Lobby verlassen", attributeObject);
                    if (!aktuelleLobby.equals("")) {
                        m.addAttribute(AKTUELLELOBBY, "");
                    }
                    return lobbyservice.spielerVerlaesstLobby(lobbyId, attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
    }

    /**
     * api/lobby/neu stößt beim lobbyservice das erstellen einer neuen Lobby an und
     * gibt diese zurück.
     *
     * @param m später Prinzipal prinz (eingeloggter User)
     * @return neu erstellte Lobby
     */
    @PostMapping(value = "neu", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage neueLobbyErstellen(Model m, @ModelAttribute(AKTUELLELOBBY) String aktuelleLobby) {
        // GET /api/lobby/neu - erstellen einer neuen Lobby ueber den LobbyService
        // zurueckgesendet wird die neu erstellte Lobbyinstanz, damit das Frontend auf
        // die Lobbyseite mit der im Backend erstellten LobbyID weiterleidten kann.
        if (aktuelleLobby.equals("")) {
            modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
            attributeObject = null;
            if (modelAttributeGefunden) {
                try {
                    attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                    if (attributeObject != null) {
                        logger.info("POST /api/lobby/neu  Von : {}", attributeObject);
                        String lobbyID = lobbyservice.lobbyErstellen(attributeObject.toString())
                                .getlobbyID();
                        return new LobbyMessage(NachrichtenCode.NEUE_LOBBY, false, lobbyID);
                    }
                } catch (NullPointerException e) {
                    logger.error(String.valueOf(e));
                }
            }
        }
        return new LobbyMessage(NachrichtenCode.BEREITS_IN_ANDERER_LOBBY, true, aktuelleLobby);
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
        logger.info("GET /api/lobby/{}", id);
        return lobbyservice.getLobbyById(id);
    }

    /**
     * Laesst einen Spieler einer Zufaelligen Lobby joinen.
     *
     * @param m Model aus dem der Nutzername ausgelesen wird.
     * @return
     */

    @PostMapping(value = "/joinRandom", produces = MediaType.APPLICATION_JSON_VALUE)
    public LobbyMessage lobbyBeitretenZufaellig(Model m) {
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.lobbyBeitretenZufaellig(attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
    }

    /**
     * Initialisiert den Spielstart einer Lobby
     *
     * @param lobbyId ID der Lobby die gestartet werden soll
     * @return
     */
    @PostMapping("/{lobbyId}/start")
    public LobbyMessage startGame(@PathVariable String lobbyId) {
        return lobbyservice.starteCountdown(lobbyId);
    }

    /**
     * Setzt das SessionAttribut "aktuelleLobby" auf leer.
     *
     * @param m Model dessen Sessionattribut "aktuelleLobby" genutzt wird
     */
    @PostMapping("/reset")
    public void resetID(Model m) {
        m.addAttribute(AKTUELLELOBBY, "");
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
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.setSpielerlimit(lobbyId, spielerlimit, attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
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
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.setPrivacy(lobbyId, istPrivat, attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
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
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.setHost(lobbyId, host, attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }
        }
        return nichtEingeloggtLobbyFehlerMessage;
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
    public LobbyMessage deleteTeilnehmer(@PathVariable String lobbyId, @RequestBody Spieler zuEntfernendSpieler, Model m) {
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.removeSpieler(lobbyId, zuEntfernendSpieler, attributeObject.toString());
                }
            } catch (NullPointerException e) {
                logger.error(String.valueOf(e));
            }

        }
        return nichtEingeloggtLobbyFehlerMessage;
    }

    /**
     * Ändert bei richtiger Berechtigung den Host der Loby.
     *
     * @param lobbyId der zu ändernden Lobby
     * @param levelID die ID des Levels, das eingestellt werrden soll.
     * @param m
     * @return LobbyMessage mit information über Erfolg/Misserfolg
     */
    @PatchMapping("/{lobbyId}/level")
    public LobbyMessage patchLevel(@PathVariable String lobbyId, @RequestBody Long levelID, Model m) {
        modelAttributeGefunden = m.containsAttribute(LOGGEDINBENUTZERNAME);
        attributeObject = null;
        if (modelAttributeGefunden) {
            try {
                attributeObject = m.getAttribute(LOGGEDINBENUTZERNAME);
                if (attributeObject != null) {
                    return lobbyservice.setLevel(lobbyId, levelID, attributeObject.toString());
                }
            } catch (Exception e) {
                logger.error(String.valueOf(e));
            }

        }
        return nichtEingeloggtLobbyFehlerMessage;
    }

    /**
     * Bekommen des Lobby Scores seit letzter Spielbeendung.
     *
     * @param lobbyId der Lobby
     */
    @GetMapping("/{lobbyId}/score")
    public LobbyMessage getScore(@PathVariable String lobbyId) {
        return lobbyservice.getScoreByLobbyId(lobbyId);
    }
}
