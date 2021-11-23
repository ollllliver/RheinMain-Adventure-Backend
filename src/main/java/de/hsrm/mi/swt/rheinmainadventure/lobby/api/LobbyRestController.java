package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;


@RestController
@RequestMapping(value={"/api/lobby/*","/api/lobby"})
public class LobbyRestController {
// Hier ist die REST Schnittstelle fuer /api/lobby/... Jede REST Anfrage auf diese Domain geht hierueber und wird in dieser Klasse bearbeitet.
// Auf die Lobbyinstanzen kann ueber den LobbyService zugegriffen werden (autowired).
    Logger logger = LoggerFactory.getLogger(LobbyRestController.class);

    @Autowired
    LobbyService lobbyservice;

    @GetMapping("alle")
    public List<Lobby> api_lobby_get_alle() {
// GET /api/lobby/alle - liefert alle Lobbys.
        logger.info("GET /api/lobby/alle");
        return lobbyservice.getLobbies();
    }

    @PostMapping("/join/{lobbyId}/{spielername}/{spielerid}")
    public List<Lobby> lobbieBeitretenMitLink(@PathVariable String lobbyId,/* eigendlich ohen aber jetzt mit: */@PathVariable String spielername, @PathVariable Integer spielerid) {
// POST /api/lobby/join/{lobbyId} - stoesst beim lobbyservice das hinzufuegen des Sessionscope Users in die mitgegebene Lobby an.
// TODO dieses Item steht an verschiedenen Stellen, weil es durchgereicht wird, aber noch mal: Spieler soll aus SessionScope gezogen werden.
        logger.info("POST /api/lobby/join/" + lobbyId + "/" + spielername + "/" + spielerid);
        lobbyservice.joinLobbybyId(lobbyId, new Spieler(spielerid, spielername));

        return null;
    }


    @GetMapping("neu")
    public ResponseEntity<Lobby> neueLobbyErstellen() {
// GET /api/lobby/neu - erstellen einer neuen Lobby ueber den LobbyService 
// zurueckgesendet wird die neu erstellte Lobbyinstanz, damit das Frontend auf die Lobbyseite mit der im Backend erstellten LobbyID weiterleidten kann.
        logger.info("GET /api/lobby/neu");
        return ResponseEntity.ok().header("Content-Type: JSON").body(lobbyservice.lobbyErstellen());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lobby> lobby_id_get(@PathVariable String id) {
// GET /api/lobby/{id} - gibt die Lobby ueber die ID zurueck
        logger.info("GET /api/lobby/" + id);
        return ResponseEntity.ok().header("Content-Type: JSON").body(lobbyservice.getLobbyById(id));
    }

}
