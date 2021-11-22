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
    /* Pseudo BenutzerService zum accessen der Datenbank um nutzernamen abzugleichen und abzufragen
     @Autowired
     private BenutzerService benutzerService;   
    */

    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    // LobbyService injected and used as instance
    public LobbyController(){
    }

    @MessageMapping("/topic/lobby/create")
    @SendTo("/topic/lobby/USERNAMEVONHOSTGEHASHED") //Hier noch HASH Anpassen
    public String createLobby(String lobbyId) throws Exception {
        lg.info(lobbyId);
        //Lobby Initialisieren und dem LobbyService in der LobbyListe hinzufügen
        lobbyService.createLobby().setIstGestartet(true);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    //Initialisiert das Starten einer Lobby also von Lobby -> Interner Spielwechsel
    @MessageMapping("/lobby/{lobbyId}/start")
    @SendTo("/lobby/{lobbyId}")
    public int startGame(@DestinationVariable String lobbyId) throws Exception {
        int lobbyStartCountdown = 10;
        
        //TODO Lobby-Countdown Starten

        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        currLobby.setIstGestartet(true);

        return lobbyStartCountdown;
    }

    //Unter der nutzung eines Lobby-Links wird ein nutzer der jeweiligen Lobby zugewiesen.
    @SubscribeMapping("/topic/lobby/{lobbyId}")
    @SendTo("/topic/lobby/{lobbyId}")
    public String lobbieBeitretenMitLink(@DestinationVariable String lobbyId/*,@SessionAttribute("username") String username*/) throws Exception{
        Lobby currLobby = lobbyService.getLobbyById(lobbyId);
        if(!currLobby.getIstGestartet() && !currLobby.getIstVoll()){
            /*
            Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session Benutzernamen findet.
            Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username); */

            Benutzer testNutzer = new Benutzer("jerry","testy");

            //Unter der Kondition dass das Spielerlimit noch nicht erreicht wurde, wird ein neuer Spieler hinzugefügt.
            currLobby.nutzerHinzufuegen(testNutzer);
        }
        ObjectMapper objectMapper = new ObjectMapper();

        return "Sie sind der Lobby "+currLobby.getlobbyID()+" gejoined "+ objectMapper.writeValueAsString(lobbyService.getLobbies());
    }

    //Post Mapping auf die seite wo sich der Random-Join Button befindet.
    @PostMapping("/lobby")
    public void lobbieBeitretenZufaellig(@SessionAttribute("username") String username){

        Lobby tempLobby = null;
        for (Lobby currLobby : lobbyService.getLobbies()) {
            if(!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()){
                tempLobby = currLobby;
                break;
            }
        }
        /*
        Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session Benutzernamen findet.
        Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username); */
        Benutzer testNutzer = new Benutzer("jerry","testy");
        if (tempLobby!=null){
            tempLobby.nutzerHinzufuegen(testNutzer);
        }else{
            lg.info("Ungueltige Lobby ID wurde angegeben.");
            //TODO : Redirect zur Homepage und Fehlermeldung Popup mit dem Logger Infotext.
        }
    }

}
