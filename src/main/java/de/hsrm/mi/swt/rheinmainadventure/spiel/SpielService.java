package de.hsrm.mi.swt.rheinmainadventure.spiel;
import java.util.List;

import javax.persistence.Tuple;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

/**
 * Interface für die Spiel Service Klasse
 * Implementation ist in {@link de.hsrm.mi.swt.rheinmainadventure.spiel.SpielServiceImpl}.
 */
public interface SpielService {

     // Das hier ist das Interface fuer aktionen auf die Spielinstanzen.
    // Alles, was an einem Spiel gemacht wird, soll hierueber passieren.
    public Spiel spielErstellen(Lobby lobby);

    public Spiel getSpielByLobbyId(String id);

    public List<Spiel> getSpiele();

    public List<Spieler> getSpielerListeByLobbyId(String id);

    public void setSpielerPosition(String id, String name, Tuple position);
    
}
