package de.hsrm.mi.swt.rheinmainadventure.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class LobbyController {

    private LobbyService lobbyService;
    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    // LobbyService injected and used as instance
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/topic/lobby/create")
    @SendTo("/topic/lobby/create")
    public String createLobby(String msg) throws Exception {
        lg.info(msg);
        lobbyService.lobbyErstellen();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/topic/lobby/started")
    public String startGame(@PathVariable String lobbyId) throws Exception {

        // Internen Lobby Timer Starten
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    @GetMapping("/lobby/{lobbyId}")
    @ResponseBody
    public void joinLobby(@PathVariable String lobbyId/* ,@SessionAttribute("username") String username */) {

        Lobby currLobby = lobbyService.getLobbyById(lobbyId);

        if (currLobby != null) {
            if (currLobby.getIstGestartet() && !currLobby.getIstVoll()) {
                // Neue "Benutzer"-Instant erstellen und anhand von Session ID username
                // abgleichen und diesen User in die Lobby Stellen
                // Player tempPlayer = PlayerService.getPlayerByUsername(username);

                // Unter der Kondition dass das Spielerlimit noch nicht erreicht wurde, wird ein
                // neuer Spieler hinzugefügt.
                if (currLobby.getTeilnehmerliste().size() < currLobby.getSpielerlimit()) {
                    // currLobby.getPlayerList().add(tempPlayer);
                }
            }
        } else {
            lg.info("Ungueltige Lobby: " + lobbyId);
        }
    }

}
