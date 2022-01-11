package de.hsrm.mi.swt.rheinmainadventure.lobby;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LobbyServiceImplTest {
    Logger logger = LoggerFactory.getLogger(LobbyServiceImplTest.class);

    @Autowired
    LobbyService lobbyService;

    public boolean containsName(final List<Spieler> list, final String name) {
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }

    /* Test für joinen bei bereits voller lobby */
    @Test
    void testJoinLobbybyId() {
        Lobby testLobby = lobbyService.lobbyErstellen("Test-User"); // Dieser User erstellt die Lobby ist aber noch
                                                                    // nicht in der Lobby drinne. vllt beim Erstellen
                                                                    // doch autoJoinen
        testLobby.setSpielerlimit(3);

        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Test-User");

        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Test-User"));
    }

    @Test
    void testJoinFullLobby() {
        Lobby testLobby = lobbyService.lobbyErstellen("Test-User2");
        testLobby.setSpielerlimit(3);
        // 4 Mal Joinen
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 2");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 3");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 4");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 5");

        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Spieler 2"));
        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Spieler 3"));
        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Spieler 4"));
        // Sollte der Lobby nicht gejoint sein
        assertFalse(containsName(testLobby.getTeilnehmerliste(), "Spieler 5"));
    }

    @Test
    void testJoinGestarteteLobby() {
        Lobby testLobby = lobbyService.lobbyErstellen("Test-User3");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Test-User3");

        testLobby.setIstGestartet(true);
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Bongo");
        assertFalse(containsName(testLobby.getTeilnehmerliste(), "Bongo"));
    }

    @Test
    void testJoinPrivateLobbyOhneLink() {
        Lobby testLobby = lobbyService.lobbyErstellen("Test-User4");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Test-User4");
        testLobby.setIstPrivat(true);

        // Sollte zufaellig einer lobby Joinen jedoch ist die einzige freie Privat
        lobbyService.lobbyBeitretenZufaellig("Bingo");
        assertFalse(containsName(testLobby.getTeilnehmerliste(), "Bingo"));
    }

    /* Prueft ob eine Lobby nach Countdown als Gestartet Gilt OHNE REST ANSTOSS */
    @Test
    void testStarteCountdown() throws Exception {

        Lobby chandsLobby = lobbyService.lobbyErstellen("Chand");

        assertFalse(chandsLobby.getIstGestartet());
        String lobbyID = chandsLobby.getlobbyID();

        lobbyService.starteCountdown(lobbyID);
        TimeUnit.SECONDS.sleep(11);
        assertTrue(chandsLobby.getIstGestartet());
        /*
         * TODO: TEST für später: Spielstart-übermittlung Test
         * Nur Host kann spiel Starten. (Also als nicht host starten versuchen und soll
         * nicht klappen) <- muss noch implementiert werden
         * mit voller lobby starten
         */
    }

    /*
     * Prueft ob eine Lobby nach dem Timeout noch Exsistiert. OHNE REST ANSTOSS
     * Hierfuer muss im Backend der Timeout auf 15 Sekunden und nicht 10 Minuten
     * gesetzt werden.
     */
    @Test
    void testTimeout() throws Exception {
        Lobby chandsLobby = lobbyService.lobbyErstellen("Chand");

        assertNotNull(chandsLobby);
        TimeUnit.SECONDS.sleep(16);
        assertNotNull(lobbyService.getLobbyById(chandsLobby.getlobbyID()));
    }

}
