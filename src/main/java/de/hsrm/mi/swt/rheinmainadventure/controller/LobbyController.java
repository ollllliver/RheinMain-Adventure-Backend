package de.hsrm.mi.swt.rheinmainadventure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.services.LobbyService;


@Controller
class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    /* Pseudo BenutzerService zum nutzen der Datenbank um nutzernamen abzugleichen und abzufragen
     @Autowired
     private BenutzerService benutzerService;   
    */

    Logger lg = LoggerFactory.getLogger(LobbyController.class);
    public LobbyController(){
    }

    /* Handler Methode für das erstellen einer Lobby */
    @MessageMapping("/topic/lobby/create")
    @SendTo("/topic/lobby/USERNAMEVONHOSTGEHASHED")
    public String createLobby(String lobbyId) throws Exception {
        /*TODO "USERNAMEVONHOSTGEHASHED" muss vom Frontend noch mitgereicht werden über Publish 
        Danach Hash generieren und per STOMP Service fürs senden (und NICHT SendTo) an alle Subscribed User der Lobby senden
        bei Lobby erstellung dann den Hash als Parameter zur lobby erstellung mitgeben*/
        lg.info(lobbyId);
        lobbyService.createLobby();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    //Mapped Frontendseitiges spielstart event an Backend seitige Spielstart funktion
    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/lobby/{lobbyId}")
    public int startGame(@DestinationVariable String lobbyId) throws Exception {
        int lobbyStartCountdown = 10;
        
        //TODO Lobby-Countdown Starten Intern über LobbyService und per STOMP an Frontend senden

        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        currLobby.setIstGestartet(true);

        return lobbyStartCountdown;
    }

        /* Unter der nutzung eines Lobby-Links wird ein nutzer der jeweiligen Lobby zugewiesen 
    unter der Kondition dass eine Lobby nicht voll und nicht gestartet ist */
    @SubscribeMapping("/topic/lobby/{lobbyId}")
    @SendTo("/topic/lobby/{lobbyId}")
    public String lobbieBeitretenMitLink(@DestinationVariable String lobbyId/*,@SessionAttribute("username") String username*/) throws Exception{
        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        if(!currLobby.getIstGestartet() && !currLobby.getIstVoll()){
            /*
            * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
            * Benutzernamen findet. 
            * Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username);
            */
            //Dummy User Data da Service nochnicht Implementiert wurde
            Benutzer testNutzer = new Benutzer("jerry","testy");
            currLobby.nutzerHinzufuegen(testNutzer);
        }
        /*TODO : Abfangen ob Lobby voll ist oder gestartet und je nach case Fehelermeldung Bauen und per
        MessageService methode an die passende Lobby {lobbyId} senden.  */	
        
        ObjectMapper objectMapper = new ObjectMapper();

        return "Sie sind der Lobby "+currLobby.getlobbyID()+" gejoined "+ objectMapper.writeValueAsString(lobbyService.getLobbies());
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
