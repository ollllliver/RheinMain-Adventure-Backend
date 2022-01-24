package de.hsrm.mi.swt.rheinmainadventure.lobby;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Mobiliar;
import de.hsrm.mi.swt.rheinmainadventure.entities.Mobiliartyp;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LobbyServiceImplTest {
    Logger logger = LoggerFactory.getLogger(LobbyServiceImplTest.class);

    @Autowired
    LobbyService lobbyService;
    @Autowired
    private IntBenutzerRepo benutzerRepository;
    @Autowired
    private MobiliarRepository mobiliarRepository;
    @Autowired
    private LevelService levelService;

    public boolean containsName(final List<Spieler> list, final String name) {
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }
    
    /**
     * Vorher ein von JPA verwaltetes Demolevel erstellen.
     */
    @BeforeEach
    @Transactional
    void setUp() {
        Benutzer ersteller = new Benutzer("Glogomir", "Strings");
        benutzerRepository.save(ersteller);


        Mobiliar rein = new Mobiliar("Box", "static/gltf/models_embedded/Box_regular.gltf", Mobiliartyp.EINGANG);
        Mobiliar raus = new Mobiliar("Box", "static/gltf/models_embedded/Box_regular.gltf", Mobiliartyp.AUSGANG);
        Mobiliar ente = new Mobiliar("Ente", "static/gltf/duck_embedded/Duck.gltf", Mobiliartyp.NPC);

        mobiliarRepository.save(rein);
        mobiliarRepository.save(raus);
        mobiliarRepository.save(ente);

        Raum raum = new Raum(0, new ArrayList<>());

        RaumMobiliar raumMobiliar1 = new RaumMobiliar(rein, raum, 4, 5);
        RaumMobiliar raumMobiliar2 = new RaumMobiliar(raus, raum, 4, 6);
        raum.getRaumMobiliar().add(raumMobiliar1);
        raum.getRaumMobiliar().add(raumMobiliar2);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);

        levelService.bearbeiteLevel("Glogomir", level);
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

    }

    /**
     * Prueft ob nach beenden des Spiels ob die Ansicht zur Lobby geswitched wurde
     * @throws InterruptedException
     * @throws Exception
     */
    @Test
    void testZurueckZurLobby() throws InterruptedException{
        Lobby testLobby = lobbyService.lobbyErstellen("Hanzo");

        lobbyService.joinLobbybyId(testLobby.getlobbyID(), "Hanzo");
        lobbyService.starteCountdown(testLobby.getlobbyID());
        TimeUnit.SECONDS.sleep(11);
        assertTrue(testLobby.getIstGestartet());

        String response = lobbyService.zurueckZurLobby(testLobby.getlobbyID()).getPayload();

        assertEquals("Spiel beendet. Kehre zurück zur Lobby", response);
        assertFalse(testLobby.getIstGestartet());
    }

}
