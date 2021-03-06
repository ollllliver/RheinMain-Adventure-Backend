package de.hsrm.mi.swt.rheinmainadventure.lobby.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LobbyBeitretenTest {
    private final String ERSTER_SPIELER = "Olive";

    // orientiert an 7.3.1 Use Case Diagramm Lobby beitreten
    // https://taiga.mi.hs-rm.de/project/weitz-2021swtpro03/wiki/731-use-case-lobby-beitreten

    // UCD -> UseCaseDiagramm
    private final String ZWEITER_SPIELER = "Chand";
    Logger logger = LoggerFactory.getLogger(LobbyErstellenTest.class);
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
        MvcResult result = mockmvc.perform(post("/api/lobby/neu").session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbyMessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        Lobby lobby = lobbyService.getLobbyById(lobbyMessage.getPayload());
        assertNotNull(lobby);
        return lobby;
    }

    private LobbyMessage lobbyBeitretenREST(MockHttpSession session, String lobbyID) throws Exception {
        MvcResult result = mockmvc.perform(post("/api/lobby/join/" + lobbyID).session(session).contentType("application/json")).andReturn();
        String jsonString = result.getResponse().getContentAsString();
        LobbyMessage lobbymessage = new ObjectMapper().readValue(jsonString, LobbyMessage.class);
        assertNotNull(lobbymessage);
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
        return session;
    }

    // ###############
    // Standardablauf:
    // ###############

    @Test
    @DisplayName("Einer Lobby per ID beitreten.")
    void UCD_Lobby_beitreten() throws Exception {

        // Mit zwei verschiedenen Benutzern einloggen
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        MockHttpSession sessionChand = logIn(ZWEITER_SPIELER, ZWEITER_SPIELER);

        Lobby alteLobby = lobbyErstellenREST(sessionOliver);
        LobbyMessage lm = lobbyBeitretenREST(sessionChand, alteLobby.getlobbyID());

        Lobby neueLobby = lobbyService.getLobbyById(alteLobby.getlobbyID());

        alteLobby.getTeilnehmerliste().add(new Spieler(ZWEITER_SPIELER));

        assertEquals(lm, new LobbyMessage(NachrichtenCode.ERFOLGREICH_BEIGETRETEN, false, alteLobby.getlobbyID()));
        assertEquals(neueLobby.getClass(), Lobby.class);
        assertEquals(neueLobby, alteLobby);
    }

    // ####################################################
    // Alternative Abl??ufe/ Fehlersituationen/ Sonderf??lle:
    // ####################################################

    @Test
    @DisplayName("Einer Lobby beitreten, die es nicht gibt.")
    void UCD_Lobby_beitreten_1a1() throws Exception {
        MockHttpSession session = logIn(ERSTER_SPIELER, ERSTER_SPIELER);
        LobbyMessage lm = lobbyBeitretenREST(session, "lobbyIDgibtEsNicht123");
        assertEquals(lm, new LobbyMessage(NachrichtenCode.LOBBY_NICHT_GEFUNDEN, true));
    }

    @Test
    @DisplayName("Spieler befindet sich bereits in der selben Lobby.")
    void UCD_Lobby_beitreten_1d_1() throws Exception {
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
        assertEquals(lobbyNach1malBeitreten, lobbyNach2malBeitreten);
        assertFalse(lm1.getIstFehler());
        assertSame(NachrichtenCode.ERFOLGREICH_BEIGETRETEN, lm1.getTyp());
        assertFalse(lm2.getIstFehler());
        assertSame(NachrichtenCode.SCHON_BEIGETRETEN, lm2.getTyp());
    }

    @Test
    @DisplayName("#101 Ein Spieler darf nur in max. einer Lobby zeitgleich sein - Spieler befindet sich bereits in einer anderen Lobby.")
    void UCD_Lobby_beitreten_1d_2() throws Exception {
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
        assertFalse(lobbyService.getLobbyById(zweitelobby.getlobbyID()).getTeilnehmerliste().contains(new Spieler(ZWEITER_SPIELER)));
    }

    @Test
    @DisplayName("Einer Lobby, in der man schon als Host ist, per ID beitreten soll nichts aendern.")
    void UCD_Lobby_beitreten_1d_3() throws Exception {
        // Mit zwei verschiedenen Benutzern einloggen
        MockHttpSession sessionOliver = logIn(ERSTER_SPIELER, ERSTER_SPIELER);

        Lobby initLobby = lobbyErstellenREST(sessionOliver);
        // ein mal beitreten:
        LobbyMessage lm1 = lobbyBeitretenREST(sessionOliver, initLobby.getlobbyID());
        Lobby lobbyNach1malBeitreten = lobbyService.getLobbyById(initLobby.getlobbyID());
        // zweites mal beitreten:
        LobbyMessage lm2 = lobbyBeitretenREST(sessionOliver, initLobby.getlobbyID());
        Lobby lobbyNach2malBeitreten = lobbyService.getLobbyById(initLobby.getlobbyID());


        // Alt soll nach ein mal beitreten wie nach zwei mal beitreten sein.
        assertEquals(lobbyNach1malBeitreten, lobbyNach2malBeitreten);
        assertFalse(lm1.getIstFehler());
        assertSame(NachrichtenCode.ERFOLGREICH_BEIGETRETEN, lm1.getTyp());
        assertFalse(lm2.getIstFehler());
        assertSame(NachrichtenCode.SCHON_BEIGETRETEN, lm2.getTyp());
    }

}