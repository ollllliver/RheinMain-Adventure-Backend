package de.hsrm.mi.swt.rheinmainadventure.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class LobbyController {

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
    public String startGame(@PathVariable String lobbyId) throws Exception {

        // Internen Lobby Timer Starten
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

        /* Unter der nutzung eines Lobby-Links wird ein nutzer der jeweiligen Lobby zugewiesen 
    unter der Kondition dass eine Lobby nicht voll und nicht gestartet ist */
    @GetMapping("/lobby/{lobbyId}")
    public void lobbieBeitretenMitLink(@PathVariable String lobbyId/*,@SessionAttribute("username") String username*/) throws Exception{
        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        /*
        * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
        * Benutzernamen findet. 
        * Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username);
        */
        //Dummy User Data da Service nochnicht Implementiert wurde
        Benutzer testNutzer = new Benutzer("jerry","testy");

        currLobby.nutzerHinzufuegen(testNutzer);
        broker.convertAndSend("/topic/lobby/" + lobbyId, new LobbyMessage("neuerSpieler", lobbyId));
        /*TODO : Abfangen ob Lobby voll ist oder gestartet und je nach case Fehelermeldung Bauen und per
        MessageService methode an die passende Lobby {lobbyId} senden.  */	
        
    }

    /* Handler Methode für das zufaellige joinen einer Lobbie. */
    @PostMapping("/lobby")
    public void lobbieBeitretenZufaellig(@SessionAttribute("username") String username){

        Lobby tempLobby = null;
        for (Lobby currLobby : lobbyService.getLobbies()) {
            if(!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()){
                tempLobby = currLobby;
                break;
            }
        }
        /*Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session Benutzernamen findet.
        Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username);*/
        Benutzer testNutzer = new Benutzer("jerry","testy");
        if (tempLobby!=null){
            tempLobby.nutzerHinzufuegen(testNutzer);
        }else{
            lg.info("Ungueltige Lobby ID wurde angegeben.");
            //TODO : Redirect zur Homepage und Fehlermeldung Popup mit dem Logger Infotext.
        }
    }


}
