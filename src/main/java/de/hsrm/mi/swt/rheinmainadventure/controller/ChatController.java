// package de.hsrm.mi.swt.rheinmainadventure.controller;

// import de.hsrm.mi.swt.rheinmainadventure.model.ChatNachricht;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
// import org.springframework.stereotype.Controller;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Controller
// public class ChatController {
//     Logger logger = LoggerFactory.getLogger(SpielerController.class);

//     // @MessageMapping("/chaaat.sendMessage")
//     // @SendTo("/topic/public")
//     // public ChatNachricht sendeNachricht(@Payload ChatNachricht chatNachricht) {
//     //     logger.info("TESSST\n\n\n\n\n\n\n\n");
//     //     return chatNachricht;
//     // }

//     // @MessageMapping("/chat.addUser")
//     // @SendTo("/topic/public")
//     // public ChatNachricht addNutzer(@Payload ChatNachricht chatNachricht, SimpMessageHeaderAccessor headerAccessor) {
//     //     // Add username in web socket session
//     //     headerAccessor.getSessionAttributes().put("username", chatNachricht.getSender());
//     //     return chatNachricht;
//     // }

// }