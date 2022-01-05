package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Vorabtests für alle api Domaenen.")
class LobbyRestControllerTest {
    Logger logger = LoggerFactory.getLogger(LobbyRestControllerTest.class);

    @Autowired
    LobbyService lobbyService;

    @Autowired
    private MockMvc mockmvc;

    private final String ERSTER_SPIELER = "Oliver";
    private final String ZWEITER_SPIELER = "Chand";

    @Autowired
    private IntBenutzerRepo benutzerrepo;

    @BeforeEach
    public void initUser() {
        benutzerrepo.deleteAll();
        final Benutzer u1 = new Benutzer();
        u1.setBenutzername(ERSTER_SPIELER);
        u1.setPasswort(ERSTER_SPIELER);
        benutzerrepo.save(u1);
        final Benutzer u2 = new Benutzer();
        u2.setBenutzername(ZWEITER_SPIELER);
        u2.setPasswort(ZWEITER_SPIELER);
        benutzerrepo.save(u2);
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
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);
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
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
        new ObjectMapper().readValue(jsonString, Lobby.class);

        MockHttpSession session2 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        result = mockmvc.perform(post("/api/lobby/neu").session(session2).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        new ObjectMapper().readValue(jsonString, Lobby.class);
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
    void testPatchSpielerlimit() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
    void testPatchPrivacy() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
    void testPatchHost() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
    void testDeleteTeilnehmer() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

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
}
