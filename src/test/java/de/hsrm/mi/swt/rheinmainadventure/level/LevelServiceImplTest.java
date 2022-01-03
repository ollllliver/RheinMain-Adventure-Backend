package de.hsrm.mi.swt.rheinmainadventure.level;

import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DataJpaTest
@AutoConfigureMockMvc
class LevelServiceImplTest {

    @Autowired
    LevelService levelService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void alleLevel() {
    }

    @Test
    void getLevel() {
    }

    @Test
    void getAlleRaumeImLevel() {
    }

    @Test
    void getRaum() {
    }

    @Test
    void getMobiliarImRaum() {
    }

    @Test
    void getStartPositionImRaum() {
    }
}