package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.List;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.model.SpielerStatus;

public interface SpielService {
    void starteSpiel(Lobby lobby);
    List<Spiel> alleSpiele();
    List<Spieler> alleSpieler(String spielID);
    Spieler positionsAktualisierung(Spieler spieler, Position position);
    Spieler statusAktualisierung(Spieler spieler, SpielerStatus status);
    Spiel getSpielByLobbyId(String id);
    List<Spieler> getSpielerListeBySpiel(Spiel spiel);
    void setSpielerPosition(String id, String name, Position position);
}
