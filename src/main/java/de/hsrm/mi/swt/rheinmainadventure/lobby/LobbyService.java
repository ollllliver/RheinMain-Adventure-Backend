package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.List;

import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;

/**
 * Interface für die Lobby Service Klasse
 * Implementation ist in {@link de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyServiceImpl}.
 */
public interface LobbyService {
// Das hier ist das Interface fuer aktionen auf die Lobbyinstanzen.
// Alles, was an einer Lobby gemacht wird, soll hierueber passieren.
    public Lobby lobbyErstellen(String spielername);
    public Lobby getLobbyById(String id);
    public List<Lobby> getLobbies();
    public LobbyMessage joinLobbybyId(String id, String benutzername);
    public void starteCountdown(String lobbyId);
    public LobbyMessage lobbieBeitretenZufaellig(String username);
}
