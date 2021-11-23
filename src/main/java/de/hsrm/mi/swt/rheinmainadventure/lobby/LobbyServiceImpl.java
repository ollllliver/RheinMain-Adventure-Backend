package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@Service
public class LobbyServiceImpl implements LobbyService {
// Hier werden die Methoden des LobbyServices implementiert.
  Logger logger = LoggerFactory.getLogger(LobbyController.class);

// Der Messagebroker wird hier durch dependencyInjection eingebunden.
// Ueber ihn koennen Nachrichten ueber STOMP an die interessierten gesendet werden.
  @Autowired
  SimpMessagingTemplate broker;

// Das ist die Liste, in der Alle Lobbies gehaltenund verwaltet werden. 
  ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

  private String generateLobbyID(String benutzerName) {
// private Methode zum generieren der Lobby ID TODO: muss noch richtig gehasht werden.
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
    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        if (zeitHashWert.length() > zaehler) {
          lobbyID += zeitHashWert.charAt(zaehler);
          zaehler++;
        }
      } else {
        if (zufallsName.length() > zaehler && Character.isLetterOrDigit(zufallsName.charAt(zaehler))) {
          lobbyID += zufallsName.charAt(zaehler);
          zaehler++;
        }
      }
    }

    return lobbyID;
  }

  public Lobby lobbyErstellen() {
// Hier wird eine neue Lobby erstellt. Der Host ist der Benutzer aus dem Sessionscope.
    // TODO Name vom Spieler bekommen (aus sessionscope?)
    String spielerName = "Player1";
    //TODO: wo kommt die SpielerID her? ist das Global oder nur in der Lobby? wenn nur in der Lobby: hochzaehlen?
    Spieler host = new Spieler(1,spielerName);
    ArrayList<Spieler> players = new ArrayList<Spieler>();
    players.add(host);
    String lobbyID = generateLobbyID(spielerName);
    Lobby lobby = new Lobby(lobbyID, players, host);

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
                //@Chand das wuerde jetzt so gehen:
                broker.convertAndSend("/topic/lobby/" + lobby.getlobbyID(), new LobbyMessage("lobbyzeitAbgelaufen", lobby.getlobbyID()));
                // Das sendet an alle, die in der Lobby eingeschrieben sind die message LOBBYZEIT_ABGELAUFEN
                
                lobbies.remove(lobby);
                
            }
        }

    };
    timer.schedule(task,10*60*1000);
  }

  public ArrayList<Lobby> getLobbies() {
// Gibt ALLE Lobbies als Array zurueck
    logger.info("Anzahl lobbies:"+this.lobbies.size());
    return this.lobbies;
    // Bsp.:
    // [{"lobbyID":"4l1a7y16","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0},
    // {"lobbyID":"1r4a4l08","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}],
    // {"lobbyID":"5r4l2P44","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}]
  }

  public Lobby getLobbyById(String Id) {
// Gibt die Lobby mit uebergebener ID zurueck. Wenn nicht vorhanden, dann return 0.
    for (Lobby currLobby : lobbies) {
      if (currLobby.getlobbyID().equals(Id)) {
        return currLobby;
      }
    }
    return null;
  }

  @Override
  public void joinLobbybyId(String id, Spieler spieler) {
// Fuegt den Sessionspieler der mitgegebenen Lobby ueber die nutzerHinzufuegen() Funktion der Lobby Klasse hinzu.
// Eigendlich ohne Spieler. in der Lobby.nutzerHinzufuegen() methode muss der Spieler aus der SessionScope geholt werden
    logger.info(spieler.getName() + " will der Lobby " + id + " joinen");
    
    Lobby currLobby = getLobbyById(id);
    //TODO ueberpruefen, ob spieler bereits in der Lobby ist. Das sollte sowohl im Frontend als auch im Backend passieren. Kann auch in der Lobby Klasse Methode selbst gemacht werden.
    currLobby.nutzerHinzufuegen(spieler);
    broker.convertAndSend("/topic/lobby/" + id, new LobbyMessage("neuerSpieler", id));

  }

}