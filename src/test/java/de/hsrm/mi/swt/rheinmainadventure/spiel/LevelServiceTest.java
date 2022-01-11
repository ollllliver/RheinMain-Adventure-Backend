package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests für den LevelService.")
class LevelServiceTest {

    @BeforeEach
    void setUp() {
        Benutzer benutzer = new Benutzer();
        benutzer.setBenutzername("Glogomir");
        benutzer.setPasswort("Char-Pointer-Arrays");
        benutzer.setErstellteLevel(Collections.emptyList());

        Level level = new Level();
        level.setName("Glogomirs Pointer-Party");
        level.setBeschreibung("");
        level.setBewertung((byte) 17);
        level.setRaeume(Collections.emptyList());
        level.setErsteller(benutzer);

        Raum raum = new Raum();
        raum.setRaumIndex(0);
        raum.setLevel(level);
    }

    @AfterEach
    void tearDown() {
    }
}