package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LobbyVerlassenTest {
    private final String ERSTER_SPIELER = "Olive";

    // 7.3.1 Use Case Diagramm Lobby verlassen
    // https://taiga.mi.hs-rm.de/project/weitz-2021swtpro03/wiki/733-use-case-diagramm-lobby-verlassen

    // UCD -> UseCaseDiagramm
    private final String ZWEITER_SPIELER = "Chand";
    Logger logger = LoggerFactory.getLogger(LobbyVerlassenTest.class);
    @Autowired
    LobbyService lobbyService;
    @Autowired
    private MockMvc mockmvc;
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

    // ###############
    // Hilfsfunktionen
    // ###############

    private Lobby lobbyErstellenREST(MockHttpSession session) throws Exception {
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());
        assertNotNull(lobby);
        return lobby;
    }

    private void lobbyBeitretenREST(MockHttpSession session, String lobbyID) throws Exception {
        MvcResult result = mockmvc
                .perform(post("/api/lobby/join/" + lobbyID).session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertNotNull(lobbymessage);
    }

    private void lobbyVerlassenREST(MockHttpSession session, String lobbyID) throws Exception {
        MvcResult result = mockmvc
                .perform(delete("/api/lobby/leave/" + lobbyID).session(session).contentType("application/json"))
                .andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lm = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertNotNull(lm);
    }

    private MockHttpSession logIn(String name, String password) throws Exception {
        MockHttpSession session = new MockHttpSession();
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("benutzername", name);
        json.put("passwort", password);
        String TESTLOGINJSON = json.toString();

        logger.info(mockmvc
                .perform(post("/api/benutzer/login").session(session).content(TESTLOGINJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn().toString());
        return session;
    }

    // ###############
    // Standardablauf:
    // ###############

    @Test
    @DisplayName("Eine Lobby, in der man ist, per ID verlassen.")
    void UCD_Lobby_verlassen() throws Exception {
        // einloggen, lobby erstellen und beitreten:
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        Lobby lobby = lobbyErstellenREST(session1);
        lobbyBeitretenREST(session1, lobby.getlobbyID());
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());

        // einloggen:
        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        lobbyBeitretenREST(session2, lobby.getlobbyID());

        assertTrue(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste()
                .contains(new Spieler(ZWEITER_SPIELER)));

        // beigetretene Lobby verlassen
        lobbyVerlassenREST(session2, lobby.getlobbyID());

        assertFalse(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste()
                .contains(new Spieler(ZWEITER_SPIELER)));
    }

    // ####################################################
    // Alternative Abl??ufe/ Fehlersituationen/ Sonderf??lle:
    // ####################################################

    @Test
    @DisplayName("#98 Host weitergeben beim Verlassen - Als Lobbyhost Lobby verlassen.")
    void UCD_Lobby_verlassen_1c() throws Exception {
        // einloggen, lobby erstellen und beitreten:
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        Lobby lobby = lobbyErstellenREST(session1);
        lobbyBeitretenREST(session1, lobby.getlobbyID());
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());

        // einloggen:
        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        lobbyBeitretenREST(session2, lobby.getlobbyID());

        assertEquals(2, lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size());
        // Host ist ERSTER_SPIELER
        assertEquals(ERSTER_SPIELER, lobbyService.getLobbyById(lobby.getlobbyID()).getHost().getName());

        // Host verlaesst
        lobbyVerlassenREST(session1, lobby.getlobbyID());

        // Host wird veitergegeben zu ZWEITER_SPIELER
        assertEquals(ZWEITER_SPIELER, lobbyService.getLobbyById(lobby.getlobbyID()).getHost().getName());

    }

    @Test
    @DisplayName("#100 Lobbyinstanz l??schen bei 0 Teilnehmern - Als Lobbyhost UND letzter Teilnehmer Lobby verlassen.")
    void UCD_Lobby_verlassen_1e() throws Exception {

        // einloggen, lobby erstellen und beitreten:
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        Lobby lobby = lobbyErstellenREST(session1);
        lobbyBeitretenREST(session1, lobby.getlobbyID());
        lobby = lobbyService.getLobbyById(lobby.getlobbyID());

        assertEquals(1, lobbyService.getLobbys().size());

        // Host verlaesst
        lobbyVerlassenREST(session1, lobby.getlobbyID());

        assertEquals(0, lobbyService.getLobbys().size());

    }

}
