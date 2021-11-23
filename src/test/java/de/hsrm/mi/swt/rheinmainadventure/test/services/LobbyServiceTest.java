package de.hsrm.mi.swt.rheinmainadventure.test.services;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

import de.hsrm.mi.swt.rheinmainadventure.model.*;
import de.hsrm.mi.swt.rheinmainadventure.services.LobbyService;

public class LobbyServiceTest {
    LobbyService lobbyService;

    @BeforeEach
    void setUp() {
        lobbyService = new LobbyService();
    }

    @Test
    @DisplayName("Sollte Null returnen wenn Lobby nicht existiert")
    void testGetLobbyById() {
        // given
        String givenId = "BLIBLIBNOTFOUND";

        // when
        Lobby actual = lobbyService.getLobbyById(givenId);

        // then
        assertNull(actual);
    }

}
