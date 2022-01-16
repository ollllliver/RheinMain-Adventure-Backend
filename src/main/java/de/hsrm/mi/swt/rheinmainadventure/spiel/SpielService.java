package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.List;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public interface SpielService {
    void starteSpiel(Lobby lobby);
    List<Spiel> alleSpiele();
    List<Spieler> getSpielerListeZumSpiel(Spiel spiel);
    Spieler positionsAktualisierung(Spieler spieler, Position position);
    Spieler getSpieler(String lobbyID, String name);
    int anzahlSchluesselErhoehen(Spiel spiel);
    int anzahlSchluesselVerringern(Spiel spiel);
}
