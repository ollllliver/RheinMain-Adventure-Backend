package de.hsrm.mi.swt.rheinmainadventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.spiel.SpielService;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@Controller
public class SpielerController {

    @Autowired
    private SpielService spielService;

    private Logger logger = LoggerFactory.getLogger(SpielerController.class);


    /**
     * Stomp Mapping für das empfangen von neuen Positionen der Spieler und senden der Spieler mit neuer Position an alle Mitspieler
     * 
     * @param pos ist eine Position, die ein Spieler einnehmen möchte
     * @param lobbyID ist die ID der Lobby, in der der Spieler gerade spielt.
     * @param name ist der Name des Spielers, der seine Position ändern möchte.
     * @throws Exception
     */
    @MessageMapping("/topic/spiel/{lobbyID}/pos/{name}")
    @SendTo("/topic/spiel/{lobbyID}")
    public Spieler updatePosition(@Payload Position pos, @DestinationVariable String lobbyID, @DestinationVariable String name) throws Exception {  
        logger.info("SpielerController.updatePosition: Payload=" + pos + ", lobbyID="+lobbyID + ", name: " + name);
        // broker.convertAndSend("/topic/spiel/" + lobbyID, pos); //nur Test
        Spieler spieler = spielService.getSpieler(lobbyID, name);
        return spielService.positionsAktualisierung(spieler, pos);
    }
}
