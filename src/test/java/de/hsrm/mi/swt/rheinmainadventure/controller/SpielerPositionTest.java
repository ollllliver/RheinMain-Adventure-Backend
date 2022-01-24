package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.spiel.SpielService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpielerPositionTest {
    Logger logger = LoggerFactory.getLogger(SpielerPositionTest.class);

    @Autowired
    LobbyService lobbyService;

    @Autowired
    private SpielService spielService;

    @Test
    @DisplayName("Spielerposition setzten")
    void spielerPosition1() throws Exception {
        Spieler spieler = new Spieler("Testname");
        Position pos = new Position(234.456f, 34.34f);
        Spieler res = spielService.positionsAktualisierung(spieler, pos);
        assertEquals(res.getEigenschaften().getPosition(), pos);
        assertEquals(res, spieler);
    }

    @Test
    @DisplayName("Spielerposition wirklich vom richtigen spieler setzten")
    void spielerPosition2() throws Exception {
        Position pos1 = new Position(234.456f, 34.34f);
        Spieler spieler1 = new Spieler("Testname2");
        Spieler spieler2 = new Spieler("Testname2");
        Spieler res = spielService.positionsAktualisierung(spieler1, pos1);
        assertEquals(res.getEigenschaften().getPosition(), pos1);
        assertNotEquals(spieler2.getEigenschaften().getPosition(), pos1);
    }

    @Test
    @DisplayName("Spielerposition ändern")
    void spielerPosition3() throws Exception {
        Spieler spieler = new Spieler("Testname");
        Position pos1 = new Position(234.456f, 34.34f);
        Position pos2 = new Position(354.356f, 17.12f);
        spieler.getEigenschaften().setPosition(pos1);
        Spieler res = spielService.positionsAktualisierung(spieler, pos2);
        assertEquals(res.getEigenschaften().getPosition(), pos2);
        assertNotEquals(res.getEigenschaften().getPosition(), pos1);
    }

}