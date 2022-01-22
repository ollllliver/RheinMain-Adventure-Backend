package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Spiel {

    private final String spielID;
    private final Timestamp startZeitpunkt;
    private final Level level;
    private List<Spieler> spielerListe;
    private int anzSchluessel;

    public Spiel(Lobby lobby, List<Spieler> spielerListe) {
        this.spielID = lobby.getlobbyID();
        this.level = lobby.getGewaehlteKarte();
        this.spielerListe = spielerListe;
        this.startZeitpunkt = new Timestamp(System.currentTimeMillis());
        this.anzSchluessel = 0;
    }

    public String getSpielID() {
        return spielID;
    }

    public List<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void setSpielerListe(ArrayList<Spieler> teilnehmerliste) {
        this.spielerListe = teilnehmerliste;
    }

    public Timestamp getStartZeitpunkt() {
        return startZeitpunkt;
    }

    public Level getKarte() {
        return level;
    }

    public int getAnzSchluessel() {
        return anzSchluessel;
    }

    public void setAnzSchluessel(int anzSchluessel) {
        this.anzSchluessel = anzSchluessel;
    }

}
