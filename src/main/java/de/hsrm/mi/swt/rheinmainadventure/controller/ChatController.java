package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.model.ChatNachricht;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatNachricht sendeNachricht(@Payload ChatNachricht chatNachricht) {
        return chatNachricht;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatNachricht addNutzer(@Payload ChatNachricht chatNachricht, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatNachricht.getSender());
        return chatNachricht;
    }

}