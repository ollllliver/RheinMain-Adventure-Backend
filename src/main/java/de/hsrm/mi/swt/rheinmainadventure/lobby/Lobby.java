package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public class Lobby {
    private String lobbyID;
    private ArrayList<Spieler> teilnehmerliste;
    private Spieler host;
    private boolean istVoll;
    private boolean istGestartet;
    private int spielerlimit = 5;

    public Lobby(String lobbyID, ArrayList<Spieler> teilnehmerliste, Spieler host) {
        this.lobbyID = lobbyID;
        this.teilnehmerliste = teilnehmerliste;
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
    }

    public void nutzerHinzufuegen(Spieler teilnehmer) {
        // Wenn Lobby nicht voll oder im Spiel (oder Spieler nicht schon drinnen), wird
        // der Spieler in die Teilnehmerliste aufgenommen
        // und es wird gegebenenfalls istVoll angepasst.
        // eventuell hier TODO: ueberpruefen, ob der Spieler bereits in der lobby ist.
        if (!istGestartet && !istVoll) {
            teilnehmerliste.add(teilnehmer);
            istVoll = (teilnehmerliste.size() >= spielerlimit);
        }
    }

    public ArrayList<Spieler> getTeilnehmerliste() {
        return teilnehmerliste;
    }

    public void setTeilnehmerliste(ArrayList<Spieler> teilnehmerliste) {
        this.teilnehmerliste = teilnehmerliste;
    }

    public boolean getIstVoll() {
        return this.istVoll;
    }

    public void setIstVoll(boolean istVollNeu) {
        this.istVoll = istVollNeu;
    }

    public boolean getIstGestartet() {
        return this.istGestartet;
    }

    public void setIstGestartet(boolean istGestartetNeu) {
        this.istGestartet = istGestartetNeu;
    }

    public String getlobbyID() {
        return this.lobbyID;
    }

    public void setlobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public Spieler getHost() {
        return this.host;
    }

    public void setHost(Spieler host) {
        this.host = host;
    }

    public int getSpielerlimit() {
        return spielerlimit;
    }

    public void setSpielerlimit(int spielerlimit) {
        this.spielerlimit = spielerlimit;
    }
}
