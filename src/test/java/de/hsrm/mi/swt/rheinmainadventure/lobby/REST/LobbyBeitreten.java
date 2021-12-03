package de.hsrm.mi.swt.rheinmainadventure.lobby.REST;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

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
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LobbyBeitreten {
    Logger logger = LoggerFactory.getLogger(LobbyErstellen.class);

    // orientiert an 7.3.1 Use Case Diagramm Lobby beitreten
    // https://taiga.mi.hs-rm.de/project/weitz-2021swtpro03/wiki/731-use-case-lobby-beitreten
    
    // UCD -> UseCaseDiagramm

    @Autowired
    LobbyService lobbyService;

    @Autowired
    private MockMvc mockmvc;

    private final String ERSTER_SPIELER = "Chand";
    private final String ZWEITER_SPIELER = "Chand";

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
    @DisplayName("Einer Lobby per ID beitreten.")
    public void UCD_Lobby_beitreten() throws Exception {

        // Mit zwei verschiedenen Benutzern einloggen
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MockHttpSession sessionChand = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);

        Lobby alteLobby = lobbyErstellenREST(sessionOliver);
        LobbyMessage lm = lobbyBeitretenREST(sessionChand, alteLobby.getlobbyID());

        Lobby neueLobby = lobbyService.getLobbyById(alteLobby.getlobbyID());

        alteLobby.getTeilnehmerliste().add(new Spieler(ZWEITER_SPIELER));

        assertTrue(!lm.getIstFehler());
        assertTrue(lm.getOperation() == NachrichtenCode.NEUER_MITSPIELER);
        assertTrue(neueLobby.getClass() == Lobby.class);
        assertTrue(neueLobby.equals(alteLobby));
    }

    // ####################################################
    // Alternative Abläufe/ Fehlersituationen/ Sonderfälle:
    // ####################################################

    @Test
    @DisplayName("Die ausgewählte Lobby ist nicht mehr verfügbar.")
    public void UCD_Lobby_beitreten_1a1() throws Exception {
        // TODO: Kommt in einem anderen Sprint. Lobbies werden noch nicht gelöscht.
        // Test: Die ausgewählte Lobby ist nicht mehr verfügbar. 
    }

    @Test
    @DisplayName("Einer Lobby, die es nie gab beitreten.")
    public void UCD_Lobby_beitreten_1a2() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        LobbyMessage lm = lobbyBeitretenREST(session, "lobbyIDgibtEsNicht123");
        assertTrue(lm.getIstFehler());
        assertTrue(lm.getOperation() == NachrichtenCode.BEITRETEN_FEHLGESCHLAGEN);
    }

    @Test
    @DisplayName("Spieler bekommt Beitrittslink von einem Mitspieler gesendet.")
    public void UCD_Lobby_beitreten_1b() throws Exception {
        // TODO: Test: Spieler bekommt Beitrittslink von einem Mitspieler gesendet. Das ist eher ein Frontendtest oder?
    }

    @Test
    @DisplayName("Spieler wählt zufälliger Lobby beitreten aus.")
    public void UCD_Lobby_beitreten_1c() throws Exception {
        // TODO: Test: Spieler wählt zufälliger Lobby beitreten aus.
    }

    @Test
    @DisplayName("Spieler befindet sich bereits in der selben Lobby.")
    public void UCD_Lobby_beitreten_1d_1() throws Exception {
        // Mit zwei verschiedenen Benutzern einloggen
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MockHttpSession sessionChand = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);
        
        Lobby initLobby = lobbyErstellenREST(sessionOliver);
        // ein mal beitreten:
        LobbyMessage lm1 = lobbyBeitretenREST(sessionChand, initLobby.getlobbyID());
        Lobby lobbyNach1malBeitreten = lobbyService.getLobbyById(initLobby.getlobbyID());
        // zweites mal beitreten:
        LobbyMessage lm2 = lobbyBeitretenREST(sessionChand, initLobby.getlobbyID());
        Lobby lobbyNach2malBeitreten = lobbyService.getLobbyById(initLobby.getlobbyID());

        
        // Alt soll nach ein mal beitreten wie nach zwei mal beitreten sein.
        assertTrue(lobbyNach1malBeitreten.equals(lobbyNach2malBeitreten));
        assertTrue(lm1.getIstFehler() == false);
        assertTrue(lm1.getOperation() == NachrichtenCode.NEUER_MITSPIELER);
        assertTrue(lm2.getIstFehler() == false);
        assertTrue(lm2.getOperation() == NachrichtenCode.SCHON_BEIGETRETEN);
    }

    @Test
    @DisplayName("Spieler befindet sich bereits in einer anderen Lobby.")
    public void UCD_Lobby_beitreten_1d_2() throws Exception {
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MockHttpSession sessionChand = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);

        Lobby ersteLobby = lobbyErstellenREST(sessionOliver);
        lobbyBeitretenREST(sessionOliver, ersteLobby.getlobbyID());

        Lobby zweitelobby = lobbyErstellenREST(sessionChand);
        lobbyBeitretenREST(sessionChand, ersteLobby.getlobbyID());

        // erster Lobby mit dem Spieler der zweiten Lobby versuchen beizutreten, solte nicht gehen
        lobbyBeitretenREST(sessionChand, zweitelobby.getlobbyID());

        // also sollte neuer Spieler danach nicht in neuer Lobby sein aber in alter Lobby.
        assertTrue(lobbyService.getLobbyById(ersteLobby.getlobbyID()).getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));
        assertFalse(lobbyService.getLobbyById(ersteLobby.getlobbyID()).getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));
    }

    @Test
    @DisplayName("Einer Lobby, in der man schon als Host ist, per ID beitreten soll nichts aendern.")
    public void UCD_Lobby_beitreten_1d_3() throws Exception {
        // // TODO: todo für eine spätere userstorry/ späterer sprint
        // Spieler neueSpieler = new Spieler("Peter"); // So?
        // // Login...
        // Lobby lobbyInDerManIst = lobbyErstellenREST();
        // // Lobby beitreten, ohne den Benutzer vorher zu wechseln, sollte nichts an der
        // // Lobby aendern.
        // Lobby neueLobby = lobbyBeitretenREST(lobbyInDerManIst.getlobbyID());
        // // Alt soll wie neu sein.
        // assertTrue(neueLobby.equals(lobbyInDerManIst));
    }

}