package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("SpielService Tests")
// TODO: Damit das funktioniert, sollte hier @ActiveProfiles("test") stehen
class SpielServiceTest {

  @Autowired
  private IntBenutzerRepo benutzerRepository;

  @Autowired
  private MobiliarRepository mobiliarRepository;

  @Autowired
  LobbyService lobbyService;

  @Autowired
  LevelService levelService;

  @Autowired
  SpielService spielService;

  Level level;

  Lobby lobbyA;
  Spiel spielA;

  Lobby lobbyB;
  Spiel spielB;

  List<Spieler> spielerListeA = new ArrayList<Spieler>();
  List<Spieler> spielerListeB = new ArrayList<Spieler>();

  @BeforeEach
  @Transactional
  void init() {
    Benutzer ersteller = new Benutzer("Glogomir", "Strings");
    benutzerRepository.save(ersteller);

    Mobiliar rein = new Mobiliar("Box", "gltf/models_embedded/Box_regular.gltf", Mobiliartyp.EINGANG);
    Mobiliar raus = new Mobiliar("Box", "gltf/models_embedded/Box_regular.gltf", Mobiliartyp.AUSGANG);
    Mobiliar ente = new Mobiliar("Ente", "gltf/duck_embedded/Duck.gltf", Mobiliartyp.NPC);

    mobiliarRepository.save(rein);
    mobiliarRepository.save(raus);
    mobiliarRepository.save(ente);

    Raum raum = new Raum(0, new ArrayList<>());

    RaumMobiliar raumMobiliar1 = new RaumMobiliar(rein, raum, 4, 5);
    RaumMobiliar raumMobiliar2 = new RaumMobiliar(raus, raum, 4, 6);
    raum.getRaumMobiliar().add(raumMobiliar1);
    raum.getRaumMobiliar().add(raumMobiliar2);

    List<Raum> raume = new ArrayList<>();
    raume.add(raum);

    Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);

    Spieler spielerHostA = new Spieler("HostA");
    Spieler spielerTeilnehmerA = new Spieler("TeilnehmerA");
    Spieler spielerTeilnehmerB = new Spieler("TeilnehmerB");

    lobbyA = lobbyService.lobbyErstellen("HostA");
    lobbyA.setlobbyID("1");
    spielerListeA.add(spielerHostA);
    spielerListeA.add(spielerTeilnehmerA);
    spielerListeA.add(spielerTeilnehmerB);
    lobbyA.setGewaehlteKarte(level);

    for (Spieler spieler : spielerListeA) {
      lobbyService.joinLobbybyId(lobbyA.getlobbyID(), spieler.getName());
    }

    spielService.starteSpiel(lobbyA);
    spielA = spielService.getSpielByLobbyId(lobbyA.getlobbyID());
  }

  @Test
  @Transactional
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
