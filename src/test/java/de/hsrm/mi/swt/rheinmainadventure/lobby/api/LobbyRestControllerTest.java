package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Vorabtests für alle api Domaenen.")
@ActiveProfiles("test")
class LobbyRestControllerTest {
    private final String ERSTER_SPIELER = "Olive";
    private final String ZWEITER_SPIELER = "Chand";
    Logger logger = LoggerFactory.getLogger(LobbyRestControllerTest.class);
    @Autowired
    LobbyService lobbyService;
    @Autowired
    private IntBenutzerRepo benutzerRepository;
    @Autowired
    private MobiliarRepository mobiliarRepository;
    @Autowired
    private LevelService levelService;
    @Autowired
    private MockMvc mockmvc;
    @Autowired
    private IntBenutzerRepo benutzerrepo;

    @BeforeEach
    @Transactional
    void setUp() {
        final Benutzer u1 = new Benutzer();
        u1.setBenutzername(ERSTER_SPIELER);
        u1.setPasswort(ERSTER_SPIELER);
        benutzerrepo.save(u1);
        final Benutzer u2 = new Benutzer();
        u2.setBenutzername(ZWEITER_SPIELER);
        u2.setPasswort(ZWEITER_SPIELER);
        benutzerrepo.save(u2);

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


        // Noch ein Level
        Raum raum1 = new Raum(0, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);


        List<Raum> raume1 = new ArrayList<>();
        raume1.add(raum1);

        Level level1 = new Level("Test-Level2", "Test-Beschreibung", (byte) 5, raume1);

        levelService.bearbeiteLevel("Glogomir", level1);
    }

    private MockHttpSession logIn(String name, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("benutzername", name);
        json.put("passwort", password);
        String TESTLOGINJSON = json.toString();

        logger.info(mockmvc.perform(
                        post("/api/benutzer/login").session(session)
                                .content(TESTLOGINJSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn().toString());
        return session;
    }

    @Test
    void testNeueLobbyErstellen() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());
        assertTrue(lobby instanceof Lobby);
        assertEquals(lobbyService.getLobbys().size(), 1);
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()), lobby);
    }

    @Test
    void testlobbyBeitretenByID() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        result = mockmvc
                .perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session).contentType("application/json"))
                .andReturn();
        jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertEquals(lobbymessage.getIstFehler(), false);
        assertSame(lobbymessage.getTyp(), NachrichtenCode.ERFOLGREICH_BEIGETRETEN);
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size(), 1);
    }

    @Test
    void testLobbyBeitretenZufaellig() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        result = mockmvc.perform(post("/api/lobby/joinRandom").session(session).contentType("application/json"))
                .andReturn();
        jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertEquals(lobbymessage.getIstFehler(), false);
        assertSame(lobbymessage.getTyp(), NachrichtenCode.ERFOLGREICH_BEIGETRETEN);
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size(), 1);
    }

    @Test
    void testVerlasseLobby() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1).contentType("application/json"))
                .andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc
                .perform(
                        post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();
        jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);

        result = mockmvc.perform(
                        delete("/api/lobby/leave/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();
        jsonString = result.getResponse().getContentAsString();
        lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertEquals(lobbymessage.getIstFehler(), false);
        assertSame(lobbymessage.getTyp(), NachrichtenCode.MITSPIELER_VERLAESST);
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size(), 1);
    }

    @Test
    void testGetLobbyById() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        result = mockmvc
                .perform(get("/api/lobby/" + lobby.getlobbyID()).session(session).contentType("application/json"))
                .andReturn();
        jsonString = result.getResponse().getContentAsString();
        Lobby restLobby = new ObjectMapper().readValue(jsonString, Lobby.class);

        assertTrue(restLobby instanceof Lobby);
        assertEquals(restLobby, lobby);
    }

    @Test
    void testGetAlleLobbys() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        new ObjectMapper().readValue(jsonString, LobbyMessage.class);

        MockHttpSession session2 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        result = mockmvc.perform(post("/api/lobby/neu").session(session2).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertEquals(lobbyService.getLobbys().size(), 2);

        result = mockmvc.perform(get("/api/lobby/alle").session(session1).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        Lobby[] lobbyListArray = new ObjectMapper().readValue(jsonString, Lobby[].class);
        ArrayList<Lobby> restLobbyList = new ArrayList<>(Arrays.asList(lobbyListArray));

        assertEquals(lobbyService.getLobbys(), restLobbyList);
    }

    @Test
    void testStartGame() {

    }

    @Test
    @DisplayName("#102 Als Host LobbyEinstellungen ändern - Spielerlimit ändern")
    void testPatchSpielerlimit() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1).contentType("application/json"))
                .andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc
                .perform(
                        post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();

        // session1 ist host

        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/spielerlimit").session(session1).content("5")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getSpielerlimit(), 5);
        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/spielerlimit").session(session1).content("2")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getSpielerlimit(), 2);
        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/spielerlimit").session(session2).content("5")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getSpielerlimit(), 2);
    }

    @Test
    @DisplayName("#102 Als Host LobbyEinstellungen ändern - istPrivat ändern")
    void testPatchPrivacy() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1)
                .contentType("application/json")).andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).
                contentType("application/json")).andReturn();

        // session1 ist host

        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/privacy").session(session1).content("true")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getIstPrivat(), true);
        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/privacy").session(session1).content("false")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getIstPrivat(), false);
        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/privacy").session(session2).content("true")
                .contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getIstPrivat(), false);
    }

    @Test
    @DisplayName("#102 Als Host LobbyEinstellungen ändern - Host ändern")
    void testPatchHost() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1).contentType("application/json"))
                .andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc
                .perform(
                        post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();
        // session1 ist host

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("istHost", false);
        json.put("name", ZWEITER_SPIELER);
        String ZWEITER_SPIELER_JSON = json.toString();

        // aktuelle Lobby holen
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());
        List<Spieler> teilnehmer = lobby.getTeilnehmerliste();
        Spieler nichtHost = teilnehmer.get(1);
        Spieler host = teilnehmer.get(0);

        // vorher host isHost true und nichtHost isHost false
        assertTrue(host.isHost());
        assertFalse(nichtHost.isHost());

        assertEquals(lobby.getHost(), host);
        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/host").session(session1)
                .content(ZWEITER_SPIELER_JSON).contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getHost(), nichtHost);

        // aktuelle Lobby holen
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());
        teilnehmer = lobby.getTeilnehmerliste();
        nichtHost = teilnehmer.get(1);
        host = teilnehmer.get(0);

        // nachher nichtHost isHost true und host isHost false
        assertTrue(nichtHost.isHost());
        assertFalse(host.isHost());

        // Jetzt noch mal mit der selben session versuchen, den Host wieder zurück zu
        // wechslen
        // Das sollte nicht gehen, also es soll sich nichts ändern, da das nur der Host
        // machen darf

        json = JsonNodeFactory.instance.objectNode();
        json.put("istHost", false);
        json.put("name", ERSTER_SPIELER);
        ZWEITER_SPIELER_JSON = json.toString();

        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/host").session(session1)
                .content(ZWEITER_SPIELER_JSON).contentType("application/json")).andReturn();
        assertEquals(lobbyService.getLobbyById(lobby.getlobbyID()).getHost(), nichtHost);
        // aktuelle Lobby holen
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());
        teilnehmer = lobby.getTeilnehmerliste();
        nichtHost = teilnehmer.get(1);
        host = teilnehmer.get(0);

        // nachher nichtHost isHost true und host isHost false
        assertTrue(nichtHost.isHost());
        assertFalse(host.isHost());

    }

    @Test
    @DisplayName("#97 Als Host Leute rausschmeißen.")
    void testDeleteTeilnehmer() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1).contentType("application/json"))
                .andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc
                .perform(
                        post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();
        // session1 ist host

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("istHost", false);
        json.put("name", ERSTER_SPIELER);
        String ERSTER_SPIELER_JSON = json.toString();

        json = JsonNodeFactory.instance.objectNode();
        json.put("istHost", false);
        json.put("name", ZWEITER_SPIELER);
        String ZWEITER_SPIELER_JSON = json.toString();

        // aktuelle Lobby holen
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());

        // Spieler 1 und 2 sind in Lobby
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ERSTER_SPIELER)));
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));
        // Spieler1 Schmeißt Spieler2 raus
        mockmvc.perform(delete("/api/lobby/" + lobby.getlobbyID() + "/teilnehmer").session(session1)
                .content(ZWEITER_SPIELER_JSON).contentType("application/json")).andReturn();
        // Spieler 2 ist nicht mehr in Lobby
        assertFalse(lobby.getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));

        result = mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session2)
                .contentType("application/json")).andReturn();

        // Spieler 1 und 2 sind in Lobby
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));
        // Spieler 2 schmeißt Spieler 1 raus
        mockmvc.perform(delete("/api/lobby/" + lobby.getlobbyID() + "/teilnehmer").session(session2)
                .content(ERSTER_SPIELER).contentType("application/json")).andReturn();
        // Spieler 1 ist immer noch in der Lobby, weil 2 kein admin ist
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ERSTER_SPIELER)));
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));

        // Spieler 1 schmeißt Spieler 1 raus
        mockmvc.perform(delete("/api/lobby/" + lobby.getlobbyID() + "/teilnehmer").session(session1)
                .content(ERSTER_SPIELER_JSON).contentType("application/json")).andReturn();
        // Spieler 1 ist immer noch in der Lobby, so verlässt man nicht die Lobby,
        // dafür haben wir den leave Button!
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ERSTER_SPIELER)));
        assertTrue(lobby.getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));

    }

    @Test
    @DisplayName("#102 Als Host LobbyEinstellungen ändern - Level ändern")
    void testPatchLevel() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        Long LOBBYDEMOID1 = alleLevel.get(0).getLevelId();
        Long LOBBYDEMOID2 = alleLevel.get(1).getLevelId();
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());

        mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session1).contentType("application/json"))
                .andReturn();

        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        result = mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session2).contentType("application/json"))
                .andReturn();
        // session1 ist host

        // TODO: Level mit ID 2 anlegen. Brauche Hilfe von Friedrich dafür. LG Olli

        // aktuelle Lobby holen
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());

        // vorher lobbyLevel ID = LOBBYDEMOID
        assertEquals(lobby.getGewaehlteKarte().getLevelId(), LOBBYDEMOID1);

        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/level").session(session1)
                .content(Long.toString(LOBBYDEMOID2)).contentType("application/json")).andReturn();
        assertEquals(lobby.getGewaehlteKarte().getLevelId(), LOBBYDEMOID2);

        // Jetzt noch mal mit der anderen session versuchen, das Level zu wechslen
        // Das sollte nicht gehen, also es soll sich nichts ändern, da das nur der Host
        // machen darf

        mockmvc.perform(patch("/api/lobby/" + lobby.getlobbyID() + "/level").session(session2)
                .content(Long.toString(LOBBYDEMOID1)).contentType("application/json")).andReturn();
        assertEquals(lobby.getGewaehlteKarte().getLevelId(), LOBBYDEMOID2);


    }
}
