package de.hsrm.mi.swt.rheinmainadventure.lobby.REST;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;

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

    // ###############
    // Hilfsfunktionen
    // ###############

    private Lobby lobbyErstellenREST() throws Exception {
        MvcResult  result = mockmvc.perform(post("/api/lobby/neu").contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(lobby instanceof Lobby);
        return lobby;
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

    private void logIn(String name) throws Exception {
        String benutzerJSON = "{benutzername: " + name + ", passwort: " + name + "}";
        mockmvc.perform(get("/api/benutzer/login").contentType("application/json").content(benutzerJSON)).andReturn();
    }

    // ###############
    // Standardablauf:
    // ###############

    @Test
    @DisplayName("Am Anfang sollte keine Lobby vorhanden sein.")
    public void vorabtest() throws Exception {
        assertTrue(lobbyService.getLobbies().size() == 0);
    }
    
    
    @Test
    @DisplayName("Eine Lobby ueber REST erstellen.")
    public void UCD_Lobby_erstellen_1() throws Exception {
        logIn("Oliver");
        lobbyErstellenREST();
        assertTrue(lobbyService.getLobbies().size() == 1);
        assertTrue(lobbyService.getLobbies().get(0).getClass() == Lobby.class);
    }

    @Test
    @DisplayName("Eine Lobby ueber REST erstellen UND auch ueber REST die Anzahl der Lobbies ueberpruefen.")
    public void UCD_Lobby_erstellen_2() throws Exception {
        lobbyErstellenREST();
        ArrayList<Lobby> lobbies = lobbiesAbfragenREST();
        assertTrue(lobbies.size() == 1);
    }

    // ####################################################
    // Alternative Abläufe/ Fehlersituationen/ Sonderfälle:
    // ####################################################


    @Test
    @DisplayName("Spieler will Lobby hosten, ist aber bereits Mitglied einer anderen Lobby.")
    public void UCD_Lobby_erstellen_1a_1() throws Exception {
        // TODO: Test: Spieler will Lobby hosten, ist aber bereits Mitglied einer anderen Lobby.
    }

    @Test
    @DisplayName(" Spieler will Lobby hosten, ist aber bereits HOST einer anderen Lobby")
    public void UCD_Lobby_erstellen_1a_2_1() throws Exception {
        lobbyErstellenREST();
        lobbyErstellenREST();
        assertTrue(lobbyService.getLobbies().size() == 1);
    }

    @Test
    @DisplayName("Viele Lobbies ueber REST erstellen und auch ueber REST die Anzahl der Lobbies ueberpruefen.")
    public void UCD_Lobby_erstellen_1a_2_2() throws Exception {
        lobbyErstellenREST();
        lobbyErstellenREST();
        ArrayList<Lobby> lobbies = lobbiesAbfragenREST();
        assertTrue(lobbies.size() == 1);
    }

    @Test
    @DisplayName("Eine Lobby ueber REST erstellen und ueber die ID ueber REST wieder abfragen.")
    public void get_api_id() throws Exception {
        Lobby lobby = lobbyErstellenREST();
        String lobbyID = lobby.getlobbyID();
        Lobby restLobby = lobbyAbfragenREST(lobbyID);
        assertTrue(restLobby.equals(lobby));
    }

}