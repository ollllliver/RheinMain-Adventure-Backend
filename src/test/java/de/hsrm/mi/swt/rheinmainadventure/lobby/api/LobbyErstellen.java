package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
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
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LobbyErstellen {
    Logger logger = LoggerFactory.getLogger(LobbyErstellen.class);

    // orientiert an 7.3.1 Use Case Diagramm Lobby hosten (und Spiel starten)
    // https://taiga.mi.hs-rm.de/project/weitz-2021swtpro03/wiki/732-use-case-diagramm-lobby-hosten-und-spiel-starten
    
    // UCD -> UseCaseDiagramm

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

    // ###############
    // Hilfsfunktionen
    // ###############

    private Lobby lobbyErstellenREST(MockHttpSession session) throws Exception {
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(lobby instanceof Lobby);
        return lobby;
    }

    private LobbyMessage lobbyBeitretenREST(MockHttpSession session, String lobbyID) throws Exception {
        MvcResult result = mockmvc.perform(post("/api/lobby/join/" + lobbyID).session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        return lobbymessage;
    }

    private Lobby lobbyAbfragenREST(String lobbyID) throws Exception {
        MvcResult  result = mockmvc.perform(get("/api/lobby/" + lobbyID).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby restLobby = new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(restLobby instanceof Lobby);
        return restLobby;
    }

    private ArrayList<Lobby> lobbiesAbfragenREST() throws Exception {
        MvcResult  result = mockmvc.perform(get("/api/lobby/alle").contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        ArrayList<Lobby> restLobbies = new ObjectMapper().readValue(jsonString, new TypeReference<ArrayList<Lobby>>(){});
        return restLobbies;
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
        logger.info("msg");
        return session;
    }

    // ###############
    // Standardablauf:
    // ###############

    @Test
    @DisplayName("Am Anfang sollte keine Lobby vorhanden sein.")
    public void vorabtest() throws Exception {
        assertTrue(lobbyService.getLobbies().size() == 0);
        assertTrue(lobbiesAbfragenREST().size() == 0);
    }
    
    
    @Test
    @DisplayName("Eine Lobby ueber REST erstellen.")
    public void UCD_Lobby_erstellen_1() throws Exception {
        // einloggen:
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        lobbyErstellenREST(session);
        assertTrue(lobbyService.getLobbies().size() == 1);
        assertTrue(lobbyService.getLobbies().get(0).getClass() == Lobby.class);
    }

    @Test
    @DisplayName("Eine Lobby ueber REST erstellen UND auch ueber REST die Anzahl der Lobbies ueberpruefen.")
    public void UCD_Lobby_erstellen_2() throws Exception {
        // einloggen:
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        lobbyErstellenREST(session);
        ArrayList<Lobby> lobbies = lobbiesAbfragenREST();
        assertTrue(lobbies.size() == 1);
    }

    // ####################################################
    // Alternative Abläufe/ Fehlersituationen/ Sonderfälle:
    // ####################################################


    @Test
    @DisplayName("Spieler will Lobby hosten, ist aber bereits Mitglied einer anderen Lobby.")
    public void UCD_Lobby_erstellen_1a_1() throws Exception {
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MockHttpSession sessionChand = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);

        Lobby ersteLobby = lobbyErstellenREST(sessionOliver);
        lobbyBeitretenREST(sessionOliver, ersteLobby.getlobbyID());

        // erster Lobby mit neuem Spieler beitreten
        lobbyBeitretenREST(sessionChand, ersteLobby.getlobbyID());

        // neuer Spieler versucht, neue Lobby zu hosten
        Lobby zweitelobby = lobbyErstellenREST(sessionChand);


        // also Lobby zwei sollte nicht erstellt worden sein.
        assertTrue(zweitelobby==null);
    }

    @Test
    @DisplayName(" Spieler will Lobby hosten, ist aber bereits HOST einer anderen Lobby")
    public void UCD_Lobby_erstellen_1a_2_1() throws Exception {
        // einloggen:
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        lobbyErstellenREST(session);
        lobbyErstellenREST(session);
        assertTrue(lobbyService.getLobbies().size() == 1);
    }

    @Test
    @DisplayName("Viele Lobbies ueber REST erstellen und auch ueber REST die Anzahl der Lobbies ueberpruefen.")
    public void UCD_Lobby_erstellen_1a_2_2() throws Exception {
        // einloggen:
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        lobbyErstellenREST(session1);
        // einloggen:
        MockHttpSession session2 = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        lobbyErstellenREST(session2);

        ArrayList<Lobby> lobbies = lobbiesAbfragenREST();
        assertTrue(lobbies.size() == 2);
    }

    @Test
    @DisplayName("Eine Lobby ueber REST erstellen und ueber die ID ueber REST wieder abfragen.")
    public void get_api_id() throws Exception {
        // einloggen:
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        Lobby lobby = lobbyErstellenREST(session);
        String lobbyID = lobby.getlobbyID();
        Lobby restLobby = lobbyAbfragenREST(lobbyID);
        assertTrue(restLobby.equals(lobby));
    }

}