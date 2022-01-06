package de.hsrm.mi.swt.rheinmainadventure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

    //@SendTo("/topic/spiel")
    @MessageMapping("/topic/spiel")
    public void updatePosition(String string) {
        logger.info("\n\n\nUpdate Position: " + string);
        //spielService.setSpielerPosition(id, name, position);
    }

}
