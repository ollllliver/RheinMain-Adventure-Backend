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
    SpielService spielService;

    @Autowired
    SimpMessagingTemplate broker;

    Logger logger = LoggerFactory.getLogger(SpielerController.class);

    /**
     * 
     * 
     * @param s
     * @param lobbyID
     * @param name
     * @throws Exception
     */
    @MessageMapping("/topic/spiel/{lobbyID}/pos/{name}")
    @SendTo("/topic/spiel/{lobbyID}")
    public Spieler updatePosition(@Payload Position pos, @DestinationVariable String lobbyID,
            @DestinationVariable String name) throws Exception {
        logger.info("SpielerController.updatePosition: Payload=" + pos + ", lobbyID=" + lobbyID + ", name: " + name);
        // broker.convertAndSend("/topic/spiel/" + lobbyID, pos); //nur Test
        Spieler spieler = spielService.getSpieler(lobbyID, name);
        return spielService.positionsAktualisierung(spieler, pos);
    }

    @MessageMapping("/topic/spiel/{lobbyID}/interagieren")
    // @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public void interagieren(@Payload String interagierenNamen, @DestinationVariable String lobbyID) throws Exception {
        logger.info("ES wurde interagiert");
        spielService.anzahlSchluesselErhoehen(spielService.findeSpiel(lobbyID));
        logger.info("Anzahl Schlüssel in Spiel" + lobbyID + " beträgt"
                + spielService.findeSpiel(lobbyID).getAnzSchluessel());

    }

    @MessageMapping("/topic/spiel")
    // @SendTo("/topic/spiel/")
    public void updatePosition(@Payload String s) throws Exception {
        // logger.info("\n\n\n\n\n\nUpdate Position: " + s +"\n\n\n\n\n\n");
    }
}
