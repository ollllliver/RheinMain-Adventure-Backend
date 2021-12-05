package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Vorabtests für alle api Domaenen.")
public class LobbyRestControllerTest {
    Logger logger = LoggerFactory.getLogger(LobbyVerlassen.class);

    @Autowired
    LobbyService lobbyService;

    @Autowired
    private MockMvc mockmvc;

    private final String ERSTER_SPIELER = "Oliver";
    
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

    @Test
    void testNeueLobbyErstellen() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(lobby instanceof Lobby);
        assertTrue(lobbyService.getLobbies().size()==1);
        assertTrue(lobbyService.getLobbyById(lobby.getlobbyID()).equals(lobby));
    }

    @Test
    void testlobbieBeitretenByID() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

        result = mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertTrue(lobbymessage.getIstFehler() == false);
        assertTrue(lobbymessage.getTyp() == NachrichtenCode.NEUER_MITSPIELER);
        assertTrue(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size() == 1);
    }

    @Test
    void testLobbieBeitretenZufaellig() {

    }

    @Test
    void testVerlasseLobby() throws Exception{
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

        result = mockmvc.perform(post("/api/lobby/join/" + lobby.getlobbyID()).session(session).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);

        result = mockmvc.perform(delete("/api/lobby/leave/" + lobby.getlobbyID()).session(session).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertTrue(lobbymessage instanceof LobbyMessage);
        assertTrue(lobbymessage.getIstFehler() == false);
        assertTrue(lobbymessage.getTyp() == NachrichtenCode.MITSPIELER_VERLAESST);
        assertTrue(lobbyService.getLobbyById(lobby.getlobbyID()).getTeilnehmerliste().size() == 0);
    }
    
    @Test
    void testGetLobbyById() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);

        result = mockmvc.perform(get("/api/lobby/" + lobby.getlobbyID()).session(session).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        Lobby restLobby = new ObjectMapper().readValue(jsonString, Lobby.class);

        assertTrue(restLobby instanceof Lobby);
        assertTrue(restLobby.equals(lobby));
    }

    @Test
    void testGetAlleLobbies() throws Exception {
        MockHttpSession session1 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session1).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        new ObjectMapper().readValue(jsonString, Lobby.class);

        MockHttpSession session2 = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        result = mockmvc.perform(post("/api/lobby/neu").session(session2).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(lobbyService.getLobbies().size()==2);

        result = mockmvc.perform(get("/api/lobby/alle").session(session1).contentType("application/json")).andReturn();
        jsonString = result.getResponse().getContentAsString();
        Lobby[] lobbyListArray = new ObjectMapper().readValue(jsonString, Lobby[].class);
        ArrayList<Lobby> restLobbyList = new ArrayList<>(Arrays.asList(lobbyListArray));
        
        assertTrue(lobbyService.getLobbies().equals(restLobbyList));
    }

    @Test
    void testStartGame() {

    }
}
