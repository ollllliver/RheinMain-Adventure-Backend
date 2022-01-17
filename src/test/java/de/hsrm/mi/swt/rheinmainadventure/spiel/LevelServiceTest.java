package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LevelServiceTest {

    @Autowired
    private IntBenutzerRepo benutzerRepository;

    @Autowired
    private MobiliarRepository mobiliarRepository;

    @Autowired
    private LevelService levelService;

    @Test
    void vorabcheck() {
        assertThat(benutzerRepository).isNotNull();
        assertThat(mobiliarRepository).isNotNull();
        assertThat(levelService).isNotNull();
    }


    @Test
    @DisplayName("alleLevel() gibt einmal gespeicherte Level immer wieder zurück")
    void alleLevel() {
        // Erstelle 2 Level in den passenden Repositories
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

        level = levelService.bearbeiteLevel("Glogomir", level);


        // Noch ein Level
        Raum raum1 = new Raum(0, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);


        List<Raum> raume1 = new ArrayList<>();
        raume1.add(raum1);

        Level level1 = new Level("Test-Level2", "Test-Beschreibung", (byte) 5, raume1);

        level1 = levelService.bearbeiteLevel("Glogomir", level1);


        // Jetzt sollten die beiden Level 1 zu 1 in der zurückgegebenen Liste sein
        assertEquals(2, levelService.alleLevel().size());
        assertEquals(levelService.alleLevel().get(0), level);
        assertEquals(levelService.alleLevel().get(1), level1);
    }

    @Test
    @Transactional
    @DisplayName("getLevel() gibt ein bestimmtes Level korrekt zurück")
    void getLevel() {
        // Wir speichern uns 1 Level
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
        level = levelService.bearbeiteLevel("Glogomir", level);

        // Level aus dem Service abfragen
        Optional<Level> levelOptionalAusDemService = levelService.getLevel(level.getLevelId());

        // Ist das immer noch da?
        assertTrue(levelOptionalAusDemService.isPresent());

        Level levelAusDemService = levelOptionalAusDemService.get();

        assertEquals(level.getLevelId(), levelAusDemService.getLevelId());
        assertEquals(level.getName(), levelAusDemService.getName());
        assertEquals(level.getBeschreibung(), levelAusDemService.getBeschreibung());
        assertEquals(level.getBewertung(), levelAusDemService.getBewertung());
        assertEquals(level.getRaeume(), levelAusDemService.getRaeume());
        assertEquals(level.getErsteller(), levelAusDemService.getErsteller());

    }

    @Test
    @DisplayName("bearbeiteLevel() funktioniert mit neuen und bekannten Leveln und legt nichts Doppelt an")
    void bearbeiteLevel() {
        // Wir inserieren ein Level, das noch nicht in der DB ist
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
        levelService.bearbeiteLevel("Glogomir", level);

        // Jetzt ist 1 Level gespeichert
        assertEquals(1, levelService.alleLevel().size());

        // Wir ändern das Level hauchzart und speichern es erneut
        level.setName("Hauchzart");
        levelService.bearbeiteLevel("Glogomir", level);

        // Jetzt ist immernoch nur 1 Level gespeichert
        assertEquals(1, levelService.alleLevel().size());
    }

    @Test
    @Transactional
    @DisplayName("loescheLevel() löscht Level loyal")
    void loescheLevel() {
        // Wir machen uns ein Level
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
        levelService.bearbeiteLevel("Glogomir", level);

        // Jetzt ist 1 Level gespeichert
        assertEquals(1, levelService.alleLevel().size());
        assertTrue(levelService.getLevel(level.getLevelId()).isPresent());

        // Wir löschen es
        levelService.loescheLevel(level.getLevelId());

        // Jetzt ist es weg
        assertEquals(0, levelService.alleLevel().size());
        assertTrue(levelService.getLevel(level.getLevelId()).isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("getAlleRaumeImLevel zeigt die aktuelle Raumanzahl an und verändert nichts")
    void getAlleRaumeImLevel() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        levelService.bearbeiteLevel("Glogomir", level);

        // Wenn wir jetzt alle Räume des Levels haben wollen, bekommen wir 2
        assertEquals(2, levelService.getAlleRaumeImLevel(level).size());
    }

    @Test
    @DisplayName("getAlleRaumeImLevel() wirft NoSuchElementException, wenn es ein Level nicht gibt")
    void getAlleRaumeImLevelSchlaegtFehlWennLevelNichtInDB() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        level.setLevelId(17L);

        // Wenn wir jetzt alle Räume des Levels haben wollen, bekommen wir auf den Deckel

        assertThrows(NoSuchElementException.class, () -> levelService.getAlleRaumeImLevel(level));
    }

    @Test
    @DisplayName("getRaum liefert die richtigen Räume")
    void getRaum() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        levelService.bearbeiteLevel("Glogomir", level);

        assertEquals(raum, levelService.getRaum(level, 0));
        assertEquals(raum1, levelService.getRaum(level, 1));
    }

    @Test
    @DisplayName("getRaum geht mit falschem Level schief")
    void getRaumMitFalschemLevel() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        level.setLevelId(17L);

        assertThrows(NoSuchElementException.class, () -> levelService.getRaum(level, 0));
    }

    @Test
    @Transactional
    @DisplayName("getRaum geht mit zu hohem Level-Index schief")
    void getRaumMitRaumIndexOutOfBounds() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        levelService.bearbeiteLevel("Glogomir", level);

        // Wir haben nur 2 Räume drin, also sollte 3 bereits out of bounds sein
        assertThrows(NoSuchElementException.class, () -> levelService.getRaum(level, 3));
    }

    @Test
    @Transactional
    @DisplayName("Ein gespeichertes Mobiliar hat ein abfragbares 3D Modell")
    void getMobiliar3DModellURI() {
        Mobiliar ente = new Mobiliar("Ente", "gltf/duck_embedded/Duck.gltf", Mobiliartyp.NPC);
        mobiliarRepository.save(ente);

        assertEquals("gltf/duck_embedded/Duck.gltf", levelService.getMobiliar3DModellURI(ente.getMobiliarId()));
    }

    @Test
    @DisplayName("getMobiliarImRaum liefert für jeden Raum sein eigenes Mobiliar")
    void getMobiliarImRaum() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        Raum raum1 = new Raum(1, new ArrayList<>());

        RaumMobiliar raumMobiliar3 = new RaumMobiliar(rein, raum1, 5, 5);
        RaumMobiliar raumMobiliar4 = new RaumMobiliar(raus, raum1, 5, 6);
        RaumMobiliar raumMobiliar5 = new RaumMobiliar(ente, raum1, 4, 4);
        raum1.getRaumMobiliar().add(raumMobiliar3);
        raum1.getRaumMobiliar().add(raumMobiliar4);
        raum1.getRaumMobiliar().add(raumMobiliar5);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);
        raume.add(raum1);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        levelService.bearbeiteLevel("Glogomir", level);

        // Beide Räume sollten in der DB sein und jeweils 2 und 3 Objekte haben
        assertEquals(2, levelService.getMobiliarImRaum(raum).size());
        assertEquals(3, levelService.getMobiliarImRaum(raum1).size());

    }

    @Test
    @DisplayName("getMobiliarImRaum geht nicht, wenn man sich einen Raum ausdenkt")
    void getMobiliarImRaumOhneDBRaum() {
        // Wir machen uns ein Level mit 2 Raeumen in die DB
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

        raum.setRaumId(17L);

        // Den Raum gibt es nicht in der DB, also sollten wir jetzt auf die Finger kriegen
        assertThrows(NoSuchElementException.class, () -> levelService.getMobiliarImRaum(raum));
    }

    @Test
    @DisplayName("Startposition wird richtig wiedergegeben")
    void getStartPositionImRaum() {
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
        levelService.bearbeiteLevel("Glogomir", level);

        assertNotNull(levelService.getStartPositionImRaum(raum));
        assertEquals(new Position(4, 5), levelService.getStartPositionImRaum(raum));
    }


    @Test
    @DisplayName("Startposition muss in einem Raum existieren, sonst kann sie nicht zurückgegeben werden")
    void getStartPositionImRaumOhneStartposition() {
        Benutzer ersteller = new Benutzer("Glogomir", "Strings");
        benutzerRepository.save(ersteller);

        Mobiliar raus = new Mobiliar("Box", "gltf/models_embedded/Box_regular.gltf", Mobiliartyp.AUSGANG);
        Mobiliar ente = new Mobiliar("Ente", "gltf/duck_embedded/Duck.gltf", Mobiliartyp.NPC);

        mobiliarRepository.save(raus);
        mobiliarRepository.save(ente);

        Raum raum = new Raum(0, new ArrayList<>());

        RaumMobiliar raumMobiliar1 = new RaumMobiliar(ente, raum, 4, 5);
        RaumMobiliar raumMobiliar2 = new RaumMobiliar(raus, raum, 4, 6);
        raum.getRaumMobiliar().add(raumMobiliar1);
        raum.getRaumMobiliar().add(raumMobiliar2);

        List<Raum> raume = new ArrayList<>();
        raume.add(raum);

        Level level = new Level("Test-Level", "Test-Beschreibung", (byte) 5, raume);
        levelService.bearbeiteLevel("Glogomir", level);

        assertThrows(NoSuchElementException.class, () -> levelService.getStartPositionImRaum(raum));
    }
}