package de.hsrm.mi.swt.rheinmainadventure.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Player;

@Service
public class LobbyService {

    ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
    Logger lg = LoggerFactory.getLogger(LobbyService.class);

    public String GenerateLobbyID(String benutzerName){

        String lobbyID = "";
        
        // zufällig gemischter Benutzername
        List<String> buchstaben = Arrays.asList(benutzerName.split(""));
        Collections.shuffle(buchstaben);
        String zufallsName = "";
        for (String buchstabe : buchstaben) {
          zufallsName += buchstabe;
        }
    
        // Hash-Wert aus aktueller Zeit
        String aktZeit = java.time.LocalTime.now().toString();
        String zeitHashWert = String.valueOf(Math.abs(aktZeit.hashCode()));
        
        int zaehler = 0;
        for(int i = 0; i < 10; i++){
            if(i % 2 == 0){
              if(zeitHashWert.length() > zaehler){
                lobbyID += zeitHashWert.charAt(zaehler);
                zaehler++;
              }
            }else{
              if(zufallsName.length() > zaehler && Character.isLetterOrDigit(zufallsName.charAt(zaehler))){
                lobbyID += zufallsName.charAt(zaehler);
                zaehler++;
              }
            }
        }
    
        return lobbyID;
    }

    public Lobby createLobby() {

        //TODO : Name vom Spieler bekommen
        String spielerName = "Player1";

        Player host = new Player(0, spielerName);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(host);

        String lobbyID = GenerateLobbyID(spielerName);
        Lobby lobby = new Lobby(lobbyID, players, host);
        lg.info("Lobby mit Lobby ID :"+lobby.getlobbyID()+" wurde erstellt.");
        starteTimer(lobby);
        lobbies.add(lobby);

        return lobby;
    }

    /* Timeout Funktion für Lobbies die nach 10 Minuten Thread-Safe eine Lobby Schließt*/
    public void starteTimer(Lobby lobby){
      Timer timer = new Timer();
      TimerTask task = new TimerTask() {

          public void run(){
              if(!lobby.getIstGestartet()){

                  //TODO : per STOMP Service allen Nutzern die auf diese Aktuelle lobbyID Subscribed sind eine Fehlermeldung per Publish senden und im Frontend abfangen.  
                  lobbies.remove(lobby);
                  
              }
          }

      };
      timer.schedule(task,10*60*1000);
  }


    public ArrayList<Lobby> getLobbies() {
        return this.lobbies;
    }

    public Lobby getLobbyById(String Id){
        for(Lobby currLobby : lobbies){
            if(currLobby.getlobbyID().equals(Id)){
                return currLobby;
            }
        }
        return null;
    }
}