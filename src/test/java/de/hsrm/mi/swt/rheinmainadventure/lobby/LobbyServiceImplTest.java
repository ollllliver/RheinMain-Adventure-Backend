package de.hsrm.mi.swt.rheinmainadventure.lobby;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LobbyServiceImplTest {
    Logger logger = LoggerFactory.getLogger(LobbyServiceImplTest.class);

    @Autowired
    LobbyService lobbyService;

    public boolean containsName(final List<Spieler> list, final String name){
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }


    /*Test für joinen bei bereits voller lobby*/
    @Test
    void testJoinLobbybyId() {
        Lobby testLobby = lobbyService.lobbyErstellen("Test-User"); //Dieser User erstellt die Lobby ist aber noch nicht in der Lobby drinne. vllt beim Erstellen doch autoJoinen
        testLobby.setSpielerlimit(3);

        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Test-User");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 2");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 3");
        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Spieler 4");


        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Test-User"));
        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Spieler 2"));
        assertTrue(containsName(testLobby.getTeilnehmerliste(), "Spieler 3"));
        assertFalse(containsName(testLobby.getTeilnehmerliste(), "Spieler 4"));

        /*
        TODO : TEST für Join Bei
        -Private Lobby (Nur bei zufall join)
        -Gestartete Lobby Joinen
        */
    }

    /* Prueft ob eine Lobby nach Countdown als Gestartet Gilt OHNE REST ANSTOSS */
    @Test
    void testStarteCountdown() throws Exception {

        Lobby chandsLobby = lobbyService.lobbyErstellen("Chand");

        assertTrue(chandsLobby.getIstGestartet()==false);
        String lobbyID = chandsLobby.getlobbyID();

        lobbyService.starteCountdown(lobbyID);
        TimeUnit.SECONDS.sleep(11);
        assertTrue(chandsLobby.getIstGestartet()==true);
        /* 
        TODO : Spielstart-übermittlung Test
        Spielstart mit Leerer Lobby soll nicht klappen
        Nur Host kann spiel Starten. (Also als nicht host starten versuchen und soll nicht klappen) <- muss noch implementiert werden
        mit voller lobby starten
        */
    }

    /* Prueft ob eine Lobby nach dem Timeout noch Exsistiert. OHNE REST ANSTOSS */
    @Test
    void testTimeout() throws Exception {
        Lobby chandsLobby = lobbyService.lobbyErstellen("Chand");

        assertTrue(chandsLobby!=null);
        TimeUnit.SECONDS.sleep(16);
        assertFalse(lobbyService.getLobbyById(chandsLobby.getlobbyID())!=null);
        /* 
        TODO : Timeout Test
        Test für wenn Lobby gestartet ist, soll die lobby nach 15 Sekunden immernoch da sein weil kein Timeout wenn gestartet
        */
    }


}
