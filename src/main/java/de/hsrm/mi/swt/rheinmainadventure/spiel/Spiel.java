package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Spiel {

    private final String SpielID;
    private ArrayList<Spieler> spielerListe;
    private final Timestamp startZeitpunkt;
    private final Level level;

    public Spiel(Lobby lobby) {
        this.SpielID = lobby.getlobbyID();
        this.level = lobby.getGewaehlteKarte();
        for (int i = 0; i < lobby.getTeilnehmerliste().size(); i++) {
            lobby.getTeilnehmerliste().get(i).getEigenschaften().setPosition(null);
            this.spielerListe.add(lobby.getTeilnehmerliste().get(i));
        }
        this.startZeitpunkt = new Timestamp(System.currentTimeMillis());
    }

    public String getSpielID() {
        return SpielID;
    }

    public ArrayList<Spieler> getSpielerListe() {
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

}
