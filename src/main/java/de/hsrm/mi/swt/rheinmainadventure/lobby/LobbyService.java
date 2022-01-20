package de.hsrm.mi.swt.rheinmainadventure.lobby;

import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import java.util.List;

/**
 * Interface für die Lobby Service Klasse Implementation ist in
 * {@link de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyServiceImpl}.
 */
public interface LobbyService {
    Lobby lobbyErstellen(String spielername);

    Lobby getLobbyById(String id);

    List<Lobby> getLobbys();

    LobbyMessage joinLobbybyId(String id, String benutzername);

    LobbyMessage starteCountdown(String lobbyId);

    LobbyMessage zurueckZurLobby(String lobbyId);

    LobbyMessage lobbyBeitretenZufaellig(String username);

    LobbyMessage spielerVerlaesstLobby(String id, String spielerName);

    LobbyMessage setSpielerlimit(String id, int spielerlimit, String spielerName);

    LobbyMessage setPrivacy(String id, Boolean istPrivat, String spielerName);

    LobbyMessage setHost(String id, Spieler host, String spielerName);

    LobbyMessage removeSpieler(String id, Spieler zuEntfernendSpieler, String spielerName);
}
