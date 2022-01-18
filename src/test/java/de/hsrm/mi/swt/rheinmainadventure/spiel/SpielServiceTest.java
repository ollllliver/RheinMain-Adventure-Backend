package de.hsrm.mi.swt.rheinmainadventure.spiel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("SpielService Tests.")
class SpielServiceTest {

  

  @Autowired
  LobbyService lobbyService;

  @Autowired
  SpielService spielService;

  Logger logger = LoggerFactory.getLogger(SpielServiceTest.class);

  @Test
  void testPositionsAktualisierung() {
    Lobby testLobby = lobbyService.lobbyErstellen("test-lobby");
    List<Spieler>testSpielerliste = new ArrayList<Spieler>();
    Spieler spieler = new Spieler("test-spieler");
    testSpielerliste.add(spieler);
    new Spiel(testLobby, testSpielerliste);

    Position pos = new Position();
    spielService.positionsAktualisierung(spieler, pos);

    spielService.positionsAktualisierung(spieler, new Position(2, 3));
    Position neuePos = spieler.getEigenschaften().getPosition();
    assertEquals(2,neuePos.getX());
    assertEquals(3,neuePos.getY());

    spielService.positionsAktualisierung(spieler, new Position(0, 0, 1));
    neuePos =spieler.getEigenschaften().getPosition();
    assertEquals(0,neuePos.getX());
    assertEquals(0,neuePos.getY());
    assertEquals(1,neuePos.getZ());
  }

  @Test
  void testSchluesselAufheben(){
    Lobby testLobby = lobbyService.lobbyErstellen("test-lobby");
    List<Spieler>testSpielerliste = new ArrayList<Spieler>();
    Spieler spieler = new Spieler("test-spieler");
    testSpielerliste.add(spieler);
    Spiel testSpiel = new Spiel(testLobby, testSpielerliste);

    //Schluessel einsammeln
    spielService.anzahlSchluesselErhoehen(testSpiel);
    spielService.anzahlSchluesselErhoehen(testSpiel);

    logger.info("SCHlüssel: " + testSpiel.getAnzSchluessel());

    assertEquals(2, testSpiel.getAnzSchluessel());

    //eine Tür öffnen
    spielService.anzahlSchluesselVerringern(testSpiel);
    assertEquals(1, testSpiel.getAnzSchluessel());

  }
  
}
