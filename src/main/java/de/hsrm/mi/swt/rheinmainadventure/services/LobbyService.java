package de.hsrm.mi.swt.rheinmainadventure.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LobbyService {

  ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
  Logger lg = LoggerFactory.getLogger(LobbyService.class);

  public String GenerateLobbyID(String benutzerName) {

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

  public Lobby createLobby() {

    // TODO : Name vom Spieler bekommen
    String spielerName = "Player1";

    Player host = new Player(0, spielerName);
    ArrayList<Player> players = new ArrayList<Player>();
    players.add(host);

    String lobbyID = GenerateLobbyID(spielerName);
    Lobby lobby = new Lobby(lobbyID, players, host, this);
    lg.info("Lobby mit Lobby ID :" + lobby.getlobbyID() + " wurde erstellt.");
    lobbies.add(lobby);

    return lobby;
  }

  public ArrayList<Lobby> getLobbies() {
    return this.lobbies;
  }

  public Lobby getLobbyById(String Id) {
    for (Lobby currLobby : lobbies) {
      if (currLobby.getlobbyID().equals(Id)) {
        return currLobby;
      }
    }
    return null;
  }

  public void deleteLobbyById(String Id) {

    new Thread(() -> {

      Lobby zuEntfernendeLobby = null;
      for (Lobby currLobby : lobbies) {
        if (currLobby.getlobbyID() == Id) {
          zuEntfernendeLobby = currLobby;
        }
      }

      if (zuEntfernendeLobby != null)
        lobbies.remove(zuEntfernendeLobby);

    }).start();
  }
}