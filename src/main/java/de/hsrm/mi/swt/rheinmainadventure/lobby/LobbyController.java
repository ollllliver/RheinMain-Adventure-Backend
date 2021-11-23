package de.hsrm.mi.swt.rheinmainadventure.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
class LobbyController {
    
    @Autowired
    private LobbyService lobbyService;
    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    SimpMessagingTemplate broker;

    // LobbyService injected and used as instance
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/topic/lobby/create")
    @SendTo("/topic/lobby/create")
    public String createLobby(String msg) throws Exception {
        lg.info(msg);
        lobbyService.lobbyErstellen();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/topic/lobby/started")
    public String startGame(@PathVariable String lobbyId){

        lobbyService.starteCountdown(lobbyId);
        return "CountdownStarted;AmountInSeconds=10";
    }

    /* Unter der nutzung eines Lobby-Links wird ein nutzer der jeweiligen Lobby zugewiesen 
    unter der Kondition dass eine Lobby nicht voll und nicht gestartet ist */
    @GetMapping("/lobby/{lobbyId}")
    public void lobbieBeitretenMitLink(@PathVariable String lobbyId/*,@SessionAttribute("username") String username*/){
        String dummyUsername = "Hallo";
        lobbyService.joinLobbybyId(lobbyId,dummyUsername);
    }

    /* Handler Methode für das zufaellige joinen einer Lobbie. Benötigt im Frontend noch ein Button der das ganze anstoßen kann per POST auf Pfad oder anderweitig*/
    @PostMapping("/lobby")
    public void lobbieBeitretenZufaellig(@SessionAttribute("username") String username){
        lobbyService.lobbieBeitretenZufaellig(username);
    }


}
