package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import com.google.gson.Gson;
import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.*;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests für die Restschnittstelle LevelRestController, auf /api/level/*
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LevelRestControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private LevelRestController levelRestController;

    @Autowired
    private IntBenutzerRepo benutzerRepository;

    @Autowired
    private LevelService levelService;

    @Autowired
    private MobiliarRepository mobiliarRepository;

    @Autowired
    private RaumMobiliarRepository raumMobiliarRepository;

    @Autowired
    private RaumRepository raumRepository;

    @Autowired
    private LevelRepository levelRepository;


    @Test
    void vorabcheck() {
        assertThat(levelRestController).isNotNull();
        assertThat(mockmvc).isNotNull();
        assertThat(benutzerRepository).isNotNull();
        assertThat(levelService).isNotNull();

        assertThat(mobiliarRepository).isNotNull();
        assertThat(raumMobiliarRepository).isNotNull();
        assertThat(raumRepository).isNotNull();
        assertThat(levelRepository).isNotNull();
    }

    @BeforeEach
    @Transactional
    void setUp() {
        Benutzer ersteller = new Benutzer("Glogomir", "Strings");
        benutzerRepository.save(ersteller);


        Mobiliar rein = new Mobiliar("Box", "static/gltf/models_embedded/Box_regular.gltf", Mobiliartyp.EINGANG);
        Mobiliar raus = new Mobiliar("Box", "static/gltf/models_embedded/Box_regular.gltf", Mobiliartyp.AUSGANG);
        Mobiliar ente = new Mobiliar("Ente", "static/gltf/duck_embedded/Duck.gltf", Mobiliartyp.NPC);

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

        levelService.bearbeiteLevel("Glogomir", level1);
    }

    @Test
    @DisplayName("GET /api/level/alle liefert JSON der richtigen Größe")
    void getAlleLevel() throws Exception {
        mockmvc.perform(
                        get("/api/level/alle")
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

    }

    @Test
    @DisplayName("#120 DELETE für Level funktioniert")
    void deleteLevel() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        int groesseVorher = alleLevel.size();

        String DELETERequest = "/api/level/" + alleLevel.get(1).getLevelId();
        mockmvc.perform(
                        delete(DELETERequest)
                                .contentType("application/json"))
                .andExpect(status().isOk());

        int groesseNachher = levelService.alleLevel().size();
        assertEquals(groesseNachher, groesseVorher - 1);
    }

    @Test
    @DisplayName("#120 DELETE für Level funktioniert nicht bei falscher Level-ID")
    void deleteLevelAberDasLevelGibtEsNicht() throws Exception {
        String DELETERequest = "/api/level/5000";
        mockmvc.perform(
                        delete(DELETERequest)
                                .contentType("application/json"))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("GET /api/level/{levelID}/{raumindex} liefert kompletten Raum-Inhalt (der richtigen Länge) als JSON")
    void getRauminhalt() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();

        String GETRequest = "/api/level/" + alleLevel.get(1).getLevelId() + "/0";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/level/{FalschelevelID}/{raumindex} liefert Fehler 404")
    void getRauminhaltVonFalschemLevel() throws Exception {
        String GETRequest = "/api/level/2000/0";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/level/{mobiliarID} liefert von einem Mobiliar den passenden gltf-Link")
    void getGLTFLink() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        List<Raum> alleRaeume = levelService.getAlleRaumeImLevel(alleLevel.get(1));
        Map<Position, Mobiliar> mobiliarImRaum = levelService.getMobiliarImRaum(alleRaeume.get(0));
        Position vierVier = new Position(4, 4);
        Mobiliar testMobiliar = mobiliarImRaum.get(vierVier);

        assertEquals("static/gltf/duck_embedded/Duck.gltf", testMobiliar.getModellURI());
        long mobiliarID = testMobiliar.getMobiliarId();
        String GETRequest = "/api/level/" + mobiliarID;

        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/level/{FalschemobiliarID} liefert Fehler 404")
    void getGLTFObjectThatDoesNotExist() throws Exception {
        String GETRequest = "/api/level/2000";
        mockmvc.perform(
                        get(GETRequest))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("GET /api/level/startposition/{levelID}/{raumindex} liefert die richtige Startposition")
    void startPositionImRaum() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();

        String GETRequest = "/api/level/startposition/" + alleLevel.get(1).getLevelId() + "/0";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(5.0))
                .andExpect(jsonPath("$.y").value(5.0));
    }

    @Test
    @DisplayName("GET /api/level/startposition/{levelID}/{zuHoherRaumIndex} liefert Fehler 400")
    void startPositionImRaumGibtEsNichtWeilRaumIndexFalsch() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        String GETRequest = "/api/level/startposition/" + alleLevel.get(1).getLevelId() + "/1";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/level/startposition/{FalschelevelID}/{RaumIndex} liefert Fehler 404")
    void startPositionImRaumGibtEsNichtWeilLevelIDFalsch() throws Exception {
        String GETRequest = "/api/level/startposition/5000/1717";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("#120 Man kann für den Level-Editor eine einfache Version eines Raumes abfragen")
    void getEinfachenRauminhalt() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        Level dbLevel = alleLevel.get(1);

        String GETRequest = String.format("/api/level/einfach/%s/%d/0",
                dbLevel.getErsteller().getBenutzername(),
                dbLevel.getLevelId());

        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelID").value(dbLevel.getLevelId()))
                .andExpect(jsonPath("$.benutzername").value(dbLevel.getErsteller().getBenutzername()))
                .andExpect(jsonPath("$.levelName").value(dbLevel.getName()))
                .andExpect(jsonPath("$.levelBeschreibung").value(dbLevel.getBeschreibung()))
                .andExpect(jsonPath("$.levelInhalt").isArray())
                .andExpect(jsonPath("$.levelInhalt").isNotEmpty());
    }

    @Test
    @Transactional
    @DisplayName("#120 Man erhält für den Level-Editor einen neuen Raum, wenn es den Raum-Index noch nicht gibt.")
    void getEinfachenRauminhaltAberDerLevelIndexIstZuNiedrig() throws Exception {
        List<Level> alleLevel = levelService.alleLevel();
        Level dbLevel = alleLevel.get(0);
        int anzahlRaeumeVorher = dbLevel.getRaeume().size();

        String GETRequest = String.format("/api/level/einfach/%s/%d/1",
                dbLevel.getErsteller().getBenutzername(),
                dbLevel.getLevelId());

        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelID").value(dbLevel.getLevelId()))
                .andExpect(jsonPath("$.benutzername").value(dbLevel.getErsteller().getBenutzername()))
                .andExpect(jsonPath("$.levelName").value(dbLevel.getName()))
                .andExpect(jsonPath("$.levelBeschreibung").value(dbLevel.getBeschreibung()))
                .andExpect(jsonPath("$.levelInhalt").isArray())
                .andExpect(jsonPath("$.levelInhalt").isNotEmpty());

        alleLevel = levelService.alleLevel();
        dbLevel = alleLevel.get(0);
        int anzahlRaeumeNachher = dbLevel.getRaeume().size();

        assertEquals(anzahlRaeumeVorher, anzahlRaeumeNachher - 1);
    }

    @Test
    @DisplayName("#120 Man erhält für den Level-Editor sogar ein neues Level, wenn die Level-ID unbekannt ist.")
    void getEinfachenRauminhaltAberDasLevelGibtEsNicht() throws Exception {
        int anzahlLevelVorher = levelService.alleLevel().size();

        String GETRequest = "/api/level/einfach/Glogomir/-1/0";
        mockmvc.perform(
                        get(GETRequest)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelID").isNotEmpty())
                .andExpect(jsonPath("$.benutzername").value("Glogomir"))
                .andExpect(jsonPath("$.levelName").value(""))
                .andExpect(jsonPath("$.levelBeschreibung").value(""))
                .andExpect(jsonPath("$.levelInhalt").isArray())
                .andExpect(jsonPath("$.levelInhalt").isNotEmpty());

        int anzahlLevelNachher = levelService.alleLevel().size();
        assertEquals(anzahlLevelVorher, anzahlLevelNachher - 1);
    }

    @Test
    @DisplayName("#120 Ein RaumPOJO-JSON wird ohne Murren gespeichert")
    void putEinfachenRauminhalt() throws Exception {
        Gson gson = new Gson();
        Level levelFuerPUT = levelService.alleLevel().get(0);
        Mobiliar demoMobiliar = mobiliarRepository.findAll().get(0);

        long[][] einfacherRaumInhalt = new long[14][22];
        for (long[] yAchse : einfacherRaumInhalt) {
            Arrays.fill(yAchse, demoMobiliar.getMobiliarId());
        }

        RaumPOJO raumPOJO = new RaumPOJO(
                levelFuerPUT.getLevelId(),
                levelFuerPUT.getErsteller().getBenutzername(),
                levelFuerPUT.getName(),
                levelFuerPUT.getBeschreibung(),
                einfacherRaumInhalt
        );

        String PUTRequest = String.format("/api/level/einfach/%s/%d/0",
                levelFuerPUT.getErsteller().getBenutzername(),
                levelFuerPUT.getLevelId());
        mockmvc.perform(
                        put(PUTRequest)
                                .contentType("application/json")
                                .content(gson.toJson(raumPOJO)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("#120 Ein RaumPOJO kann man nicht speichern, wenn der Raumindex falsch ist")
    void putEinfachenRauminhaltGehtSchiefBeiFalschemRaumindex() throws Exception {
        Gson gson = new Gson();
        Level levelFuerPUT = levelService.alleLevel().get(0);
        Mobiliar demoMobiliar = mobiliarRepository.findAll().get(0);

        long[][] einfacherRaumInhalt = new long[14][22];
        for (long[] yAchse : einfacherRaumInhalt) {
            Arrays.fill(yAchse, demoMobiliar.getMobiliarId());
        }
        RaumPOJO raumPOJO = new RaumPOJO(
                levelFuerPUT.getLevelId(),
                levelFuerPUT.getErsteller().getBenutzername(),
                levelFuerPUT.getName(),
                levelFuerPUT.getBeschreibung(),
                einfacherRaumInhalt
        );

        String PUTRequest = String.format("/api/level/einfach/%s/%d/5",
                levelFuerPUT.getErsteller().getBenutzername(),
                levelFuerPUT.getLevelId());
        mockmvc.perform(
                        put(PUTRequest)
                                .contentType("application/json")
                                .content(gson.toJson(raumPOJO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("#120 Ein RaumPOJO kann man nicht speichern, wenn die Level-ID nicht existiert")
    void putEinfachenRauminhaltGehtSchiefBeiFalscherLevelID() throws Exception {
        Gson gson = new Gson();
        Level levelFuerPUT = levelService.alleLevel().get(0);

        Mobiliar demoMobiliar = mobiliarRepository.findAll().get(0);

        long[][] einfacherRaumInhalt = new long[14][22];
        for (long[] yAchse : einfacherRaumInhalt) {
            Arrays.fill(yAchse, demoMobiliar.getMobiliarId());
        }
        RaumPOJO raumPOJO = new RaumPOJO(
                levelFuerPUT.getLevelId(),
                levelFuerPUT.getErsteller().getBenutzername(),
                levelFuerPUT.getName(),
                levelFuerPUT.getBeschreibung(),
                einfacherRaumInhalt
        );

        String PUTRequest = String.format("/api/level/einfach/%s/-1/0",
                levelFuerPUT.getErsteller().getBenutzername());
        mockmvc.perform(
                        put(PUTRequest)
                                .contentType("application/json")
                                .content(gson.toJson(raumPOJO)))
                .andExpect(status().isNotFound());

    }

}