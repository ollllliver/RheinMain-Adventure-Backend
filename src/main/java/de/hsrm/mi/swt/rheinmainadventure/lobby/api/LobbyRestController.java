package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;

@RestController
@RequestMapping(value = { "/api/lobby/*", "/api/lobby" })
@SessionAttributes(names = {"loggedinBenutzername"})
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
     * stoesst beim lobbyservice das hinzufuegen des Sessionscope Users in die mitgegebene Lobby an
     * @param lobbyId
     * @param m
     * @return
     */
    @PostMapping("/join/{lobbyId}")
    public ResponseEntity<LobbyMessage> lobbieBeitretenMitLink(@PathVariable String lobbyId,Model m) {
        logger.info("POST /api/lobby/join/" + lobbyId);
        return ResponseEntity.ok().header("Content-Type: JSON").body(lobbyservice.joinLobbybyId(lobbyId, m.getAttribute("loggedinBenutzername").toString()));
    }

    @PostMapping("neu")
    public ResponseEntity<Lobby> neueLobbyErstellen(Model m) {
        // GET /api/lobby/neu - erstellen einer neuen Lobby ueber den LobbyService
        // zurueckgesendet wird die neu erstellte Lobbyinstanz, damit das Frontend auf
        // die Lobbyseite mit der im Backend erstellten LobbyID weiterleidten kann.
        logger.info("POST /api/lobby/neu");
        Lobby lobby = lobbyservice.lobbyErstellen(m.getAttribute("loggedinBenutzername").toString());
        return ResponseEntity.ok().header("Content-Type: JSON").body(lobby);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lobby> getLobbyById(@PathVariable String id) {
        // GET /api/lobby/{id} - gibt die Lobby ueber die ID zurueck
        logger.info("GET /api/lobby/" + id);
        return ResponseEntity.ok().header("Content-Type: JSON").body(lobbyservice.getLobbyById(id));
    }

    // @Chand work in progress
    
    // @PostMapping("/lobby")
    // public void lobbieBeitretenZufaellig(@SessionAttribute("username") String username){
    //     lobbyService.lobbieBeitretenZufaellig(username);
    // }

    // @MessageMapping("/lobby/{lobbyId}/start")
    // @SendTo("/topic/lobby/started")
    // public String startGame(@PathVariable String lobbyId){

    //     lobbyService.starteCountdown(lobbyId);
    //     return "CountdownStarted;AmountInSeconds=10";
    // }


}
