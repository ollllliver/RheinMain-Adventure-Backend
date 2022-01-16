package de.hsrm.mi.swt.rheinmainadventure.spiel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

  Lobby lobbyA = lobbyService.lobbyErstellen("Lobby A");
  Lobby lobbyB = lobbyService.lobbyErstellen("Lobby B");

  List<Spieler> spielerListeA = new ArrayList<Spieler>();
  List<Spieler> spielerListeB = new ArrayList<Spieler>();
  
  Spieler spielerHostA = new Spieler("HostA");
  Spieler spielerTeilnehmerA = new Spieler("TeilnehmerA");
  Spieler spielerTeilnehmerB = new Spieler("TeilnehmerB");

  @Test
  void testAlleSpieleAbrufen() {

    spielerHostA.setHost(true);
    spielerListeA.add(spielerHostA);

    spielerTeilnehmerA.setHost(false);
    spielerListeA.add(spielerTeilnehmerA);

    spielerTeilnehmerB.setHost(false);
    spielerListeA.add(spielerTeilnehmerB);

    Spiel spielA = new Spiel(lobbyA, spielerListeA);

    spielService.starteSpiel(lobbyA);
    assertEquals(1, spielService.alleSpiele().size());

    Spieler spielerHostB = new Spieler("HostB");
    spielerHostB.setHost(true);
    spielerListeB.add(spielerHostB);

    Spieler spielerTeilnehmerC = new Spieler("TeilnehmerC");
    spielerTeilnehmerC.setHost(false);
    spielerListeB.add(spielerTeilnehmerC);

    Spiel spielB = new Spiel(lobbyB, spielerListeB);
    spielService.starteSpiel(lobbyB);
    assertEquals(2, spielService.alleSpiele().size());
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
