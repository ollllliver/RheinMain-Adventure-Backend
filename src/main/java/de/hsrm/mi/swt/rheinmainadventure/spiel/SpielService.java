package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.List;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public interface SpielService {
    void starteSpiel(Lobby lobby);
    List<Spiel> alleSpiele();
    List<Spieler> getSpielerListeBySpiel(Spiel spiel);
    Spieler positionsAktualisierung(Spieler spieler, Position position);
    void anzahlSchluesselErhoehen(Spieler spieler);
    void anzahlSchluesselVerringern(Spieler spieler);
    Spieler getSpieler(String lobbyID, String name);
}
