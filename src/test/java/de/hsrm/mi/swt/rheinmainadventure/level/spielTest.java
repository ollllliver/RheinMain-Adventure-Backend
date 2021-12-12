package de.hsrm.mi.swt.rheinmainadventure.level;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.spiel.Spiel;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Erster Test für die Spielklasse.")
public class spielTest {

    @Autowired
    LobbyService lobbyService;
    
    @Test
    void einSpielErstellen() throws Exception{
        Lobby lobby = lobbyService.lobbyErstellen("spielername1");
        lobbyService.joinLobbybyId(lobby.getlobbyID(), "spielername1");
        lobbyService.joinLobbybyId(lobby.getlobbyID(), "spielername2");

        Spiel spiel = new Spiel(lobby);

        assertTrue(spiel.getSpielerListe().equals(lobby.getTeilnehmerliste()));
    }
}
