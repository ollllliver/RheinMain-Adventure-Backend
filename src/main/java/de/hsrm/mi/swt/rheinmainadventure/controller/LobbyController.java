package de.hsrm.mi.swt.rheinmainadventure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.services.LobbyService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
class LobbyController {

    private LobbyService lobbyService;
    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    // LobbyService injected and used as instance
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/lobby/create")
    @SendTo("/topic/lobby/created")
    public String createLobby() throws Exception {
        lobbyService.createLobby();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }
}
