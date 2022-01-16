package de.hsrm.mi.swt.rheinmainadventure.messaging;

/**
 * Enum für deffinierte Nachrichtencodes.
 * Bei änderungen auch die Nachrichtencodes im Frontend ändern!
 */
public enum NachrichtenCode {

    // Benachrichtigung in der LobbyÜBERSICHT
    NEUE_LOBBY, LOBBY_ENTFERNT,

    // Benachrichtigungen in der Lobby
    NEUER_MITSPIELER, MITSPIELER_VERLAESST,
    LOBBY_GESTARTET, COUNTDOWN_GESTARTET,
    BEENDE_SPIEL, LOBBYZEIT_ABGELAUFEN,
        
    // Lobby beitreten Antworten:
    ERFOLGREICH_BEIGETRETEN, BEITRETEN_FEHLGESCHLAGEN, SCHON_BEIGETRETEN,
    LOBBY_VOLL, LOBBY_NICHT_GEFUNDEN, KEINE_LOBBY_FREI,BEREITS_IN_ANDERER_LOBBY,

    // Lobby Einstellungsänderungen Antworten:
    NEUE_EINSTELLUNGEN, KEINE_BERECHTIGUNG;

}
