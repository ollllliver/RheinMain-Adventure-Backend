package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
  // Ueber ihn koennen Nachrichten ueber STOMP an die interessierten gesendet
  // werden.
  @Autowired
  SimpMessagingTemplate broker;

  // Das ist die Liste, in der Alle Lobbies gehaltenund verwaltet werden.
  ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
  
  private String generateLobbyID(String benutzerName) {
    // private Methode zum generieren der Lobby ID TODO: muss noch richtig gehasht
    // werden.
    String lobbyID = "";

    logger.info(benutzerName); // Keine Ahnung warum aber ohne das log geht die Loop nicht, VUe is komisch
    for (int i = 0; i < benutzerName.length(); i++) {

      char buchstabe = benutzerName.charAt(i);
      int newAscii = (int) buchstabe;
      String ascii = Integer.toString(newAscii);

      char newChar = benutzerName.charAt(i);
      newChar += 1;
      lobbyID = lobbyID + ascii + newChar;

    }

    logger.info("NEW HASH: " + lobbyID);

    return lobbyID;
  }
  @Override
  public Lobby lobbyErstellen() {
    // Hier wird eine neue Lobby erstellt. Der Host ist der Benutzer aus dem
    // Sessionscope.
    // TODO Name vom Spieler bekommen (aus sessionscope?)
    String spielerName = "Player1";
    // TODO: wo kommt die SpielerID her? ist das Global oder nur in der Lobby? wenn
    // nur in der Lobby: hochzaehlen?
    Spieler host = new Spieler(1, spielerName);
    ArrayList<Spieler> players = new ArrayList<Spieler>();
    players.add(host);
    String lobbyID = generateLobbyID(spielerName);
    Lobby lobby = new Lobby(lobbyID, players, host);

    starteTimeout(lobby);
    lobbies.add(lobby);

    return lobby;
  }

  /*
   * Timeout Funktion für Lobbies die nach 10 Minuten Thread-Safe eine Lobby
   * Schließt
   */
  public void starteTimeout(Lobby lobby) {
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

      public void run() {
        if (!lobby.getIstGestartet()) {

          // TODO : per STOMP Service allen Nutzern die auf diese Aktuelle lobbyID
          // Subscribed sind eine Fehlermeldung per Publish senden und im Frontend
          // abfangen.
          // @Chand das wuerde jetzt so gehen:
          broker.convertAndSend("/topic/lobby/" + lobby.getlobbyID(),
              new LobbyMessage("lobbyzeitAbgelaufen", lobby.getlobbyID()));
          // Das sendet an alle, die in der Lobby eingeschrieben sind die message
          // LOBBYZEIT_ABGELAUFEN

          lobbies.remove(lobby);

        }
      }

    };
    //timer.schedule(task, 5 * 1000); //TESTCASE
    timer.schedule(task,10 * 60 * 1000);
  }


  //Startet ein Countdown fürs setzen von IstGestartet bei 10 Sekunden
  @Override
  public void starteCountdown(String lobbyId) {
    Timer timer = new Timer();
    Lobby lobby = getLobbyById(lobbyId);
    TimerTask task = new TimerTask() {

      public void run() {
        if (!lobby.getIstGestartet()) {
            lobby.setIstGestartet(true);
        }
      }

    };
    timer.schedule(task, 10 * 1000);
  }

  public ArrayList<Lobby> getLobbies() {
    // Gibt ALLE Lobbies als Array zurueck
    logger.info("Anzahl lobbies:" + this.lobbies.size());
    return this.lobbies;
    // Bsp.:
    // [{"lobbyID":"4l1a7y16","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0},
    // {"lobbyID":"1r4a4l08","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}],
    // {"lobbyID":"5r4l2P44","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}]
  }

  public Lobby getLobbyById(String Id) {
    // Gibt die Lobby mit uebergebener ID zurueck. Wenn nicht vorhanden, dann return
    // 0.
    for (Lobby currLobby : lobbies) {
      if (currLobby.getlobbyID().equals(Id)) {
        return currLobby;
      }
    }
    return null;
  }

  @Override
  public void joinLobbybyId(String Id, String spielername) {
    // Fuegt den Sessionspieler der mitgegebenen Lobby ueber die nutzerHinzufuegen()
    // Funktion der Lobby Klasse hinzu.
    // Eigendlich ohne Spieler. in der Lobby.nutzerHinzufuegen() methode muss der
    // Spieler aus der SessionScope geholt werden
    logger.info(spielername + " will der Lobby " + Id + " joinen");

    Lobby currLobby = getLobbyById(Id);
    // TODO ueberpruefen, ob spieler bereits in der Lobby ist. Das sollte sowohl im
    // Frontend als auch im Backend passieren. Kann auch in der Lobby Klasse Methode
    // selbst gemacht werden.
    Spieler spieler = new Spieler(1,spielername);
    /*
    * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
    * Benutzernamen findet. 
    * Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username);
    */


      // Wenn Lobby nicht voll oder im Spiel (oder Spieler nicht schon drinnen), wird
      // der Spieler in die Teilnehmerliste aufgenommen
      // und es wird gegebenenfalls istVoll angepasst.
      // eventuell hier TODO: ueberpruefen, ob der Spieler bereits in der lobby ist.
      if (!currLobby.getIstGestartet() && !currLobby.getIstVoll()) {
        currLobby.getTeilnehmerliste().add(spieler);
        currLobby.setIstVoll((currLobby.getTeilnehmerliste().size() >= currLobby.getSpielerlimit()));
        broker.convertAndSend("/topic/lobby/" + Id, new LobbyMessage("neuerSpieler", Id));
      }else{
        //TODO : Fehlermeldungen anpassen und per Broker senden.
        broker.convertAndSend("/topic/lobby/" + Id, new LobbyMessage("JoinFailed", Id));
      }

  }

  @Override
  public void lobbieBeitretenZufaellig(String username) {
    Lobby tempLobby = null;
    for (Lobby currLobby : lobbies) {
        if(!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()){
            tempLobby = currLobby;
            break;
        }
    }
    /*Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session Benutzernamen findet.
    Benutzer tempNutzer = benutzerService.getBenutzerByUsername(username);*/
    Spieler testNutzer = new Spieler(1,"testy");
    if (tempLobby!=null){
        tempLobby.getTeilnehmerliste().add(testNutzer);
        broker.convertAndSend("/topic/lobby/" + tempLobby.getlobbyID(), new LobbyMessage("neuerSpieler", tempLobby.getlobbyID()));
    }else{
      //broker.convertAndSend("/topic/lobby/" + tempLobby.getlobbyID(), new LobbyMessage("keineLobbyGefunden", tempLobby.getlobbyID()));
      //Per Broker an den User der einer Random Lobby joinen wollte Fehlermeldung senden.
    }
}
    
}

