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
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

/**
 * Lobby Service für das verwalten aller Lobbys.
 * 
 */
@Service
public class LobbyServiceImpl implements LobbyService {
  Logger logger = LoggerFactory.getLogger(LobbyServiceImpl.class);

  /**
   * Der Messagebroker wird hier durch dependencyInjection eingebunden. Über ihn
   * koennen Nachrichten ueber STOMP an die Subscriber gesendet werden
   */
  @Autowired
  SimpMessagingTemplate broker;

  /**
   * Liste aller Lobbyinstanzen.
   */
  ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

  /**
   * Generiert eine einmalige Lobby-ID aus dem Namen des Spielers, kombiniert mit
   * einem Hashwert des aktuellen Zeitstempels
   * 
   * @param benutzerName Der mitgegebene Name des Spielers
   * @return Gibt die generierte Lobby-ID als String zurueck
   */
  private String generateLobbyID(String benutzerName) {
    String lobbyID = "";

    // Benutzername verschoben um eine Stelle
    String verschobenerName = "";

    for (int i = 0; i < benutzerName.length(); i++) {
      char neuerChar = benutzerName.charAt(i);
      neuerChar += 1;
      verschobenerName += neuerChar;
    }

    // Hash-Wert aus aktueller Zeit
    String aktZeit = java.time.LocalTime.now().toString();
    String zeitHashWert = String.valueOf(Math.abs(aktZeit.hashCode()));

    int zaehler = 0;

    // Kombination von Name und Zeit-Hashwert fuer Lobby-ID
    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        if (zeitHashWert.length() > zaehler) {
          lobbyID += zeitHashWert.charAt(zaehler);
          zaehler++;
        }
      } else {
        if (verschobenerName.length() > zaehler && Character.isLetterOrDigit(verschobenerName.charAt(zaehler))) {
          lobbyID += verschobenerName.charAt(zaehler);
          zaehler++;
        }
      }
    }

    return lobbyID;
  }

  /**
   * Erstellt eine neue Lobby mit dem mitgegebenen Spieler(namen) als Host.
   * 
   * @param spielerName des Lobby Erstellers
   * @return neu erstellte Lobby
   */
  @Override
  public Lobby lobbyErstellen(String spielerName) {
    // Hier wird eine neue Lobby erstellt. Der Host ist der Benutzer aus dem
    // Sessionscope.
    Spieler host = new Spieler(spielerName);
    ArrayList<Spieler> players = new ArrayList<Spieler>();
    // players.add(host);
    String lobbyID = generateLobbyID(spielerName);
    Lobby lobby = new Lobby(lobbyID, players, host);

    starteTimeout(lobby);
    lobbies.add(lobby);

    return lobby;
  }

  @Override
  public LobbyMessage spielerVerlaesstLobby(String id, String spielerName) {
    logger.info("HIER GEHTS REIN: " + id + spielerName);
    Lobby currLobby = getLobbyById(id);
    ArrayList<Spieler> teilnehmer = currLobby.getTeilnehmerliste();

    for (int i = 0; i < teilnehmer.size(); i++) {
      Spieler currSpieler = teilnehmer.get(i);
      if (currSpieler.getName().equals(spielerName)) {
        teilnehmer.remove(currSpieler);
      }
      // TODO Else noch abdecken
    }

    broker.convertAndSend("/topic/lobby/" + id, new LobbyMessage(NachrichtenCode.MITSPIELER_VERLAESST, false));
    return new LobbyMessage(NachrichtenCode.MITSPIELER_VERLAESST, false);

  }

  /*
   * Timeout Funktion für Lobbies die nach 10 Minuten Thread-Safe eine Lobby
   * Schließt
   */
  private void starteTimeout(Lobby lobby) {
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

      public void run() {
        if (!lobby.getIstGestartet()) {

          // per STOMP Service allen Nutzern die auf diese Aktuelle lobbyID
          // Subscribed sind eine Fehlermeldung per Publish senden und im Frontend
          // abfangen.
          // @Chand das wuerde jetzt so gehen:
          broker.convertAndSend("/topic/lobby/" + lobby.getlobbyID(),
              new LobbyMessage(NachrichtenCode.LOBBYZEIT_ABGELAUFEN, true));
          // Das sendet an alle, die in der Lobby eingeschrieben sind die message
          // LOBBYZEIT_ABGELAUFEN

          lobbies.remove(lobby);

        }
      }

    };
    timer.schedule(task, 15 * 1000); //für Testing auf 5 Sekunden setzen.
    //timer.schedule(task, 10 * 60 * 1000);
  }

  // Startet ein Countdown fürs setzen von IstGestartet bei 10 Sekunden
  @Override
  public LobbyMessage starteCountdown(String lobbyId) {
    Timer timer = new Timer();

    broker.convertAndSend("/topic/lobby/" + lobbyId, new LobbyMessage(NachrichtenCode.COUNTDOWN_GESTARTET, false));

    TimerTask task = new TimerTask() {

      public void run() {
        Lobby lobby = getLobbyById(lobbyId);
        if (!lobby.getIstGestartet()) {
          lobby.setIstGestartet(true);

          // TODO : Hier nach Spielcountdown Ansicht wechseln

        }
      }

    };
    timer.schedule(task, 10 * 1000); 
    return new LobbyMessage(NachrichtenCode.COUNTDOWN_GESTARTET, false,"Sekunden=10");  
  }

  /**
   * Gibt ALLE aktuellen Lobbies als Array zurueck.
   * 
   */
  public ArrayList<Lobby> getLobbies() {
    logger.info("Anzahl lobbies:" + this.lobbies.size());
    return this.lobbies;
    // Bsp.:
    // [{"lobbyID":"4l1a7y16","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0},
    // {"lobbyID":"1r4a4l08","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}],
    // {"lobbyID":"5r4l2P44","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}]
  }

  /**
   * Gibt die eine Lobby mit der übergebenen id zurück.
   * 
   * @param Id ist die Lobby ID der gewünschten Lobby
   * @return Lobby mit der mitgegebenen ID
   */
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

  /**
   * Fuegt den Sessionspieler der mitgegebenen Lobby (ID) ueber die
   * nutzerHinzufuegen() Funktion der Lobby Klasse hinzu.
   * 
   * @param Id          mitgegebene Lobby-ID
   * @param spielername Der mitgegebene Name des Spielers
   * @return Gibt eine LobbyMessage mit passendem NachrichtenCode, sowie
   *         Erfolgsstatus zurueck
   */
  @Override
  public LobbyMessage joinLobbybyId(String Id, String spielername) {
    // Eigentlich ohne Spieler. In der Lobby.nutzerHinzufuegen() Methode muss der
    // Spieler aus der SessionScope geholt werden
    logger.info(spielername + " will der Lobby " + Id + " joinen");
    
    Lobby currLobby = getLobbyById(Id);
    if(currLobby==null){
      return new LobbyMessage(NachrichtenCode.BEITRETEN_FEHLGESCHLAGEN, true);
    }
    // ueberpruefen, ob Spieler bereits in der Lobby ist
    if (!currLobby.getTeilnehmerliste().contains(new Spieler(spielername))) {

      // Wenn Lobby nicht voll oder im Spiel (oder Spieler nicht schon drinnen), wird
      // der Spieler in die Teilnehmerliste aufgenommen
      // und es wird gegebenenfalls istVoll angepasst.
      if (currLobby.getIstGestartet()) {
        return new LobbyMessage(NachrichtenCode.LOBBY_GESTARTET, true);
      } else if (currLobby.getIstVoll()) {
        return new LobbyMessage(NachrichtenCode.LOBBY_VOLL, true);
      } else {
        Spieler spieler = new Spieler(spielername);
        /*
         * Legt benutzer Instanz an wenn man einen User mit dem aktuellen Session
         * Benutzernamen findet. Benutzer tempNutzer =
         * benutzerService.getBenutzerByUsername(username);
         */
        currLobby.getTeilnehmerliste().add(spieler);
        currLobby.setIstVoll((currLobby.getTeilnehmerliste().size() >= currLobby.getSpielerlimit()));
        //broker.convertAndSend("/topic/lobby/" + Id, new LobbyMessage(NachrichtenCode.NEUER_MITSPIELER, false,currLobby.getlobbyID()));
        return new LobbyMessage(NachrichtenCode.NEUER_MITSPIELER, false,currLobby.getlobbyID());
      }
    }
    return new LobbyMessage(NachrichtenCode.SCHON_BEIGETRETEN, false);
  }

  @Override
  public LobbyMessage lobbieBeitretenZufaellig(String spielername) {
    Lobby tempLobby = null;
    for (Lobby currLobby : lobbies) {
      if (!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()) {
        tempLobby = currLobby;
        break;
      }
    }
    if (tempLobby != null) {
      return joinLobbybyId(tempLobby.getlobbyID(), spielername);
    } else {
      return new LobbyMessage(NachrichtenCode.KEINE_LOBBY_FREI, true);
    }

  }

}
