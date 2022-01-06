package de.hsrm.mi.swt.rheinmainadventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.rheinmainadventure.spiel.SpielService;

@Controller

public class SpielerController {

    @Autowired
    SpielService spielService;

    @Autowired
    SimpMessagingTemplate broker;

    Logger logger = LoggerFactory.getLogger(SpielerController.class);

    // @MessageMapping("/topic/spiel")
    // @SendTo("/topic/public")
    // public void updatePosition(@Payload Tuple position) {
    //     logger.info("Update Position\n\n\n");
    //     //spielService.setSpielerPosition(id, name, position);
    // }

    @MessageMapping("/topic/spiel/{lobbyID}/pos/{name}")
    //@SendTo("/topic/spiel/")
    public void updatePosition(@Payload String s, @DestinationVariable String lobbyID, @DestinationVariable String name) throws Exception {  
        logger.info("SpielerController.updatePosition: Payload=" + s + ", lobbyID="+lobbyID + ", name: " + name);
        spielService.setSpielerPosition(lobbyID, name, null);
    }


    @MessageMapping("/topic/spiel")
    //@SendTo("/topic/spiel/")
    public void updatePosition(@Payload String s) throws Exception {  
       // logger.info("\n\n\n\n\n\nUpdate Position: " + s +"\n\n\n\n\n\n");
    }
}
