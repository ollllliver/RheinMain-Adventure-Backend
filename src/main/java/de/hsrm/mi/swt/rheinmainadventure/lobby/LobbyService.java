package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.List;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public interface LobbyService {
// Das hier ist das Interface fuer aktionen auf die Lobbyinstanzen.
// Alles, was an einer Lobby gemacht wird, soll hierueber passieren.
    public Lobby lobbyErstellen();
    public Lobby getLobbyById(String id);
    public List<Lobby> getLobbies();
    public void joinLobbybyId(String id, Spieler spieler); // eigendlich ohen Spieler mitgeben, sondern Spieler aus session ID nehemn
}
