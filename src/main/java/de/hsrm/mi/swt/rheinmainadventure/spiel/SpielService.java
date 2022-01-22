package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import java.util.List;

public interface SpielService {
    void starteSpiel(Lobby lobby);

    List<Spiel> alleSpiele();

    Spiel findeSpiel(String lobbyID);

    List<Spieler> getSpielerListeBySpiel(Spiel spiel);

    Spiel getSpielByLobbyId(String lobbyId);

    Spieler positionsAktualisierung(Spieler spieler, Position position);

    Spieler getSpieler(String lobbyID, String name);

    int anzahlSchluesselErhoehen(Spiel spiel);

    int anzahlSchluesselVerringern(Spiel spiel);
}
