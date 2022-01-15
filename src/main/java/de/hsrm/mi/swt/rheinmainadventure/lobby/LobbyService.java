package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.List;

import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

/**
 * Interface für die Lobby Service Klasse Implementation ist in
 * {@link de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyServiceImpl}.
 */
public interface LobbyService {
    public Lobby lobbyErstellen(String spielername);

    public Lobby getLobbyById(String id);

    public List<Lobby> getLobbys();

    public LobbyMessage joinLobbybyId(String id, String benutzername);

    public LobbyMessage starteCountdown(String lobbyId);

    public LobbyMessage lobbyBeitretenZufaellig(String username);

    public LobbyMessage spielerVerlaesstLobby(String id, String spielerName);

    public LobbyMessage setSpielerlimit(String id, int spielerlimit, String spielerName);

    public LobbyMessage setPrivacy(String id, Boolean istPrivat, String spielerName);

    public LobbyMessage setHost(String id, Spieler host, String spielerName);

    public LobbyMessage removeSpieler(String id, Spieler zuEntfernendSpieler, String spielerName);
}
