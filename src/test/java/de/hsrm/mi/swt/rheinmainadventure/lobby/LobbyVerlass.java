package de.hsrm.mi.swt.rheinmainadventure.lobby;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public class LobbyVerlass {
    @Autowired
    LobbyServiceImpl lobbyService;

    @BeforeEach
    public void init() {

        lobbyService = new LobbyServiceImpl();

    }

    public Lobby generateLobby() {
        String spielerName = "Player1";
        Spieler host = new Spieler(spielerName);
        ArrayList<Spieler> players = new ArrayList<Spieler>();
        players.add(host);
        String lobbyID = lobbyService.lobbyErstellen(spielerName).getlobbyID();
        Lobby lobby = new Lobby(lobbyID, players, host);

        return lobby;
    }

    @Test
    @DisplayName("Host alleine und verlaesst Lobby")
    public void testHostverlaesst() {
        Lobby lobby = generateLobby();
        String spielerName = lobby.getHost().getName();
        lobbyService.spielerVerlaesstLobby(lobby.getlobbyID(), spielerName);

        assertTrue(lobby.getTeilnehmerliste().size() == 0);
    }

}
