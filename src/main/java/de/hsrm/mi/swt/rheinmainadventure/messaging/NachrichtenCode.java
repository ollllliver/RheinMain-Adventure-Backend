package de.hsrm.mi.swt.rheinmainadventure.messaging;

/**
 * Enum für deffinierte Nachrichtencodes.
 * Bei änderungen auch die Nachrichtencodes im Frontend ändern!
 */
public enum NachrichtenCode {
    NEUER_MITSPIELER, MITSPIELER_VERLAESST, LOBBYZEIT_ABGELAUFEN, BEITRETEN_FEHLGESCHLAGEN, SCHON_BEIGETRETEN,
    LOBBY_GESTARTET, LOBBY_VOLL, COUNTDOWN_GESTARTET, KEINE_LOBBY_FREI;
}
