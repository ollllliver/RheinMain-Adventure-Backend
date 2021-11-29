package de.hsrm.mi.swt.rheinmainadventure.lobby.REST;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;

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

    private Lobby lobbyBeitretenREST(String lobbyID) throws Exception {
        MvcResult  result = mockmvc.perform(post("/api/lobby/join/" + lobbyID).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        Lobby lobby = new ObjectMapper().readValue(jsonString, Lobby.class);
        assertTrue(lobby instanceof Lobby);
        return lobby;
    }

    // ###############
    // Standardablauf:
    // ###############

    @Test
    @DisplayName("Einer Lobby per ID beitreten.")
    public void UCD_Lobby_beitreten() throws Exception {
        // TODO: Test: Login mit einem Spieler
        Spieler spieler = new Spieler("Hans"); // So?
        // Login...
        Lobby alteLobby = lobbyErstellenREST();
        // TODO: Test: Benutzer wechseln(ueber login oder session scope)
        Spieler neueSpieler = new Spieler("Peter"); // So?
        
        Lobby neueLobby = lobbyBeitretenREST(alteLobby.getlobbyID());

        alteLobby.getTeilnehmerliste().add(neueSpieler);
        assertTrue(neueLobby.getClass() == Lobby.class);
        assertTrue(neueLobby.equals(alteLobby));
        assertTrue(neueLobby.equals(lobbyService.getLobbyById(neueLobby.getlobbyID())));
    }

    // ####################################################
    // Alternative Abläufe/ Fehlersituationen/ Sonderfälle:
    // ####################################################

    @Test
    @DisplayName("Die ausgewählte Lobby ist nicht mehr verfügbar.")
    public void UCD_Lobby_beitreten_1a1() throws Exception {
        // TODO: Test: Die ausgewählte Lobby ist nicht mehr verfügbar.
    }

    @Test
    @DisplayName("Einer Lobby, die es nie gab beitreten.")
    public void UCD_Lobby_beitreten_1a2() throws Exception {
        // TODO: Test: Die ausgewählte Lobby gab es nie.
    }

    @Test
    @DisplayName("Spieler bekommt Beitrittslink von einem Mitspieler gesendet.")
    public void UCD_Lobby_beitreten_1b() throws Exception {
        // TODO: Test: Spieler bekommt Beitrittslink von einem Mitspieler gesendet.
    }

    @Test
    @DisplayName("Spieler wählt zufälliger Lobby beitreten aus.")
    public void UCD_Lobby_beitreten_1c() throws Exception {
        // TODO: Test: Spieler wählt zufälliger Lobby beitreten aus.
    }


    @Test
    @DisplayName("Spieler befindet sich bereits in der selben Lobby.")
    public void UCD_Lobby_beitreten_1d_1() throws Exception {
        // TODO: Test: Login mit einem Spieler
        Spieler spieler = new Spieler("Peter"); // So?
        // Login...
        Lobby initLobby = lobbyErstellenREST();
        
        // TODO: Test: Login mit einem neuen Spieler
        Spieler neueSpieler = new Spieler("Peter"); // So?
        // Login...
        // ein mal beitreten:
        Lobby lobbyNach1malBeitreten = lobbyBeitretenREST(initLobby.getlobbyID());
        // zweites mal beitreten:
        Lobby lobbyNach2malBeitreten = lobbyBeitretenREST(initLobby.getlobbyID());

        // Alt soll nach ein mal beitreten wie nach zwei mal beitreten sein.
        assertTrue(lobbyNach1malBeitreten.equals(lobbyNach2malBeitreten));
    }

    @Test
    @DisplayName("Spieler befindet sich bereits in einer anderen Lobby.")
    public void UCD_Lobby_beitreten_1d_2() throws Exception {
        // TODO: Test: Login mit einem Spieler
        Spieler spieler = new Spieler("Peter"); // So?
        // Login...
        Lobby ersteLobby = lobbyErstellenREST();

        // TODO: Test: Benutzer wechseln(ueber login oder session scope)
        // zweite Lobby mit anderem Spieler erstellen, sodass der Spieler schon in einer Lobby drinnen ist:
        Spieler neueSpieler = new Spieler("Peter"); // So?
        Lobby zweitelobby = lobbyErstellenREST();

        // erster Lobby mit dem neuen Spieler versuchen beizutreten solte nicht gehen
        ersteLobby = lobbyBeitretenREST(ersteLobby.getlobbyID());

        // also sollte neuer Spieler danach nicht in neuer Lobby sein aber in alter Lobby.
        assertFalse(ersteLobby.getTeilnehmerliste().contains(neueSpieler));
        assertFalse(lobbyService.getLobbyById(ersteLobby.getlobbyID()).getTeilnehmerliste().contains(neueSpieler));
        assertTrue(zweitelobby.getTeilnehmerliste().contains(neueSpieler));
        assertTrue(lobbyService.getLobbyById(zweitelobby.getlobbyID()).getTeilnehmerliste().contains(neueSpieler));
    }

    @Test
    @DisplayName("Einer Lobby, in der man schon als Host ist, per ID beitreten soll nichts aendern.")
    public void UCD_Lobby_beitreten_1d_3() throws Exception {
        // TODO: Test: Login mit einem Spieler
        Spieler neueSpieler = new Spieler("Peter"); // So?
        // Login...
        Lobby lobbyInDerManIst = lobbyErstellenREST();
        // Lobby beitreten, ohne den Benutzer vorher zu wechseln, sollte nichts an der Lobby aendern.
        Lobby neueLobby = lobbyBeitretenREST(lobbyInDerManIst.getlobbyID());
        // Alt soll wie neu sein.
        assertTrue(neueLobby.equals(lobbyInDerManIst));
    }


}