package de.hsrm.mi.swt.rheinmainadventure.spiel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
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

  Lobby lobbyA;
  Spiel spielA;

  Lobby lobbyB;
  Spiel spielB;

  List<Spieler> spielerListeA = new ArrayList<Spieler>();
  List<Spieler> spielerListeB = new ArrayList<Spieler>();

  @BeforeEach
  void init() {
    Spieler spielerHostA = new Spieler("HostA");
    Spieler spielerTeilnehmerA = new Spieler("TeilnehmerA");
    Spieler spielerTeilnehmerB = new Spieler("TeilnehmerB");
    
    lobbyA = lobbyService.lobbyErstellen("HostA");
    spielerListeA.add(spielerHostA);
    spielerListeA.add(spielerTeilnehmerA);
    spielerListeA.add(spielerTeilnehmerB);

    for (Spieler spieler : spielerListeA) {
      lobbyService.joinLobbybyId(lobbyA.getlobbyID(), spieler.getName());
    }

    spielService.starteSpiel(lobbyA);
    spielA = spielService.getSpielByLobbyId(lobbyA.getlobbyID());
  }

  @Test
  void testAlleSpieleAbrufen() {
    List<Spiel> spieleListe = spielService.alleSpiele();
    assertNotNull(spieleListe);
  }

  @Test
  void testListeTeilnehmenderSpieler() {

  }

  @Test
  void testPositionsAktualisierung() {

  }

  @Test
  void testAlleSpielerAbrufen() {

  }

  @Test
  void testAnzahlSchluessel() {

  }
  
}
