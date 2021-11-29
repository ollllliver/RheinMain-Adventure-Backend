package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public class Lobby {
    private String lobbyID;
    private ArrayList<Spieler> teilnehmerliste;
    private Spieler host;
    private boolean istVoll;
    private boolean istGestartet;
    private boolean istPrivat;
    private int spielerlimit;

    // Aktuellen LobbyService reinreichen lassen da ich nicht weiß wie man bei einer
    // nicht Component Klasse Autowired.
    /**
     * Erstellt eine Lobby mit einer bestimmten ID
     * 
     * @param lobbyID        einmalige LobbyID für eine Lobby
     * @param teilehmerliste leere Liste der teilnehmer
     * @param host           ein Spieler der der host der Lobby ist
     * 
     */
    public Lobby(String lobbyID, ArrayList<Spieler> teilnehmerliste, Spieler host) {
        this.lobbyID = lobbyID;
        this.teilnehmerliste = teilnehmerliste;
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
        this.istPrivat = true;
        this.spielerlimit = 4;
    }

    /**
     * Es folgen nurnoch Getter und Setter
     */
    public ArrayList<Spieler> getTeilnehmerliste() {
        return teilnehmerliste;
    }

    public void setTeilnehmerliste(ArrayList<Spieler> teilnehmerliste) {
        this.teilnehmerliste = teilnehmerliste;
    }

    public boolean getIstVoll() {
        istVoll = (teilnehmerliste.size() >= spielerlimit);
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

    public boolean getIstPrivat() {
        return istPrivat;
    }

    public void setIstPrivat(boolean istPrivat) {
        this.istPrivat = istPrivat;
    }
}
