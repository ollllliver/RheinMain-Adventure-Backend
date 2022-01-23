package de.hsrm.mi.swt.rheinmainadventure.lobby;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.spiel.Spiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lobby {
    private String lobbyID;
    private ArrayList<Spieler> teilnehmerliste;
    private Spieler host;
    private boolean istVoll;
    private boolean istGestartet;
    private boolean istPrivat;
    private int spielerlimit;
    private Level gewaehlteKarte;
    private Spiel spiel;

    // Aktuellen LobbyService reinreichen lassen da ich nicht weiß wie man bei einer
    // nicht Component Klasse Autowired.

    /**
     * Erstellt eine Lobby mit einer bestimmten ID
     *
     * @param lobbyID         einmalige LobbyID für eine Lobby
     * @param teilnehmerliste leere Liste der teilnehmer
     * @param host            ein Spieler der der host der Lobby ist
     */
    public Lobby(String lobbyID, List<Spieler> teilnehmerliste, Spieler host, Level defaultlevel) {
        this.lobbyID = lobbyID;
        this.teilnehmerliste = new ArrayList<>();
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
        this.istPrivat = false;
        this.spielerlimit = 2;
        this.gewaehlteKarte = defaultlevel;
    }

    public Lobby() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lobby lobby = (Lobby) o;

        return Objects.equals(lobbyID, lobby.lobbyID);
    }

    @Override
    public int hashCode() {
        return lobbyID != null ? lobbyID.hashCode() : 0;
    }

    /**
     * Es folgen nurnoch Getter und Setter
     */

    public List<Spieler> getTeilnehmerliste() {
        return teilnehmerliste;
    }

    public void setTeilnehmerliste(List<Spieler> teilnehmerliste) {
        this.teilnehmerliste = new ArrayList<>(teilnehmerliste);
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
        if (getHost() != null) {
            getHost().setHost(false);
        }
        host.setHost(true);
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

    public Spiel getSpiel() {
        return spiel;
    }

    public void setSpiel(Spiel spiel) {
        this.spiel = spiel;
    }

    public Level getGewaehlteKarte() {
        return gewaehlteKarte;
    }

    public void setGewaehlteKarte(Level gewaehlteKarte) {
        this.gewaehlteKarte = gewaehlteKarte;
    }

    public String getScoreString() {
        String scoreString = "";
        for (int i=0; i<teilnehmerliste.size();i++){
            Spieler iterSpieler = teilnehmerliste.get(i);
            scoreString = String.format("%s%s: %s%n",scoreString, iterSpieler.getName(),iterSpieler.getScore());
        }
        return scoreString;
    }

}
