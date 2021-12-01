package de.hsrm.mi.swt.rheinmainadventure.lobby;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootTest
public class LobbyTest {

    @Autowired
    LobbyServiceImpl lobbyService;

    @BeforeEach
    public void init() {

        lobbyService = new LobbyServiceImpl();
    }

    /**
     * Testet die generierte Lobby-ID mit verschiedenen Namen und prueft sie auf laenge, 
     * Einmaligkeit und ob sie nur aus Buchstaben und Zahlen besteht.
     */
    @Test
    public void testLobbyID() {

        // zu testende Benutzernamen
        String lobbyID1 = lobbyService.generateLobbyID("Oliver");
        String lobbyID2 = lobbyService.generateLobbyID("Chand");
        String lobbyID3 = lobbyService.generateLobbyID("Raoul");
        String lobbyID4 = lobbyService.generateLobbyID("Andreas");
        String lobbyID5 = lobbyService.generateLobbyID("");

        String[] idWerte = { lobbyID1, lobbyID2, lobbyID3, lobbyID4, lobbyID5 };

        HashSet<String> idSet = new HashSet<String>();

        for (String id : idWerte) {
            // jede ID ist zwischen 5 und 10 Zeichen lang
            assertTrue(id.length() >= 5 && id.length() <= 10);

            // jede ID ist einmalig
            assertTrue(idSet.add(id));

            // die ID besteht nur aus Zahlen und Buchstaben
            for(var c : id.toCharArray()){
                assertTrue(Character.isLetterOrDigit(c));
            }
        }
    }

    @Test
    public void testLobbyErstellen() {
        String spielerName = "Player1";
        Spieler host = new Spieler(spielerName);
        ArrayList<Spieler> players = new ArrayList<Spieler>();
        players.add(host);
        String lobbyID = lobbyService.generateLobbyID(spielerName);
        Lobby lobby = new Lobby(lobbyID, players, host);

        assertNotNull(lobby);

    }

    @Test
    public void testLobbyErstellen1000() {
        ArrayList<Lobby> lobbyList = new ArrayList<Lobby>();

        for (int i = 0; i < 1000; i++) {
            String spielerName = "Player" + i;
            Spieler host = new Spieler(spielerName);
            ArrayList<Spieler> players = new ArrayList<Spieler>();
            players.add(host);
            String lobbyID = lobbyService.generateLobbyID(spielerName);
            Lobby lobby = new Lobby(lobbyID, players, host);
            lobbyList.add(lobby);
            assertNotNull(lobby);
        }

        assertTrue(lobbyList.size() == 1000);

    }

}