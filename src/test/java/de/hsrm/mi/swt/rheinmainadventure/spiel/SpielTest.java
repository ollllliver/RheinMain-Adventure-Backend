package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Erster Test für die Spielklasse.")
class SpielTest {

    @Autowired
    LobbyService lobbyService;

    @Test
    void einSpielErstellen() throws Exception {
        Lobby lobby = lobbyService.lobbyErstellen("spielername1");
        lobbyService.joinLobbybyId(lobby.getlobbyID(), "spielername1");
        lobbyService.joinLobbybyId(lobby.getlobbyID(), "spielername2");

        // TODO @Oliver
        
        // Spiel spiel = new Spiel(lobby);

        // assertEquals(spiel.getSpielerListe(), lobby.getTeilnehmerliste());
    }
}
