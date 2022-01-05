package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.sql.Timestamp;
import java.util.ArrayList;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;

public class Spiel {
    private ArrayList<Spieler> spielerListe;
    private Timestamp startZeitpunkt;
    private Karte karte;
    private String id;

    public Spiel(Lobby lobby) {
        this.karte = lobby.getGewaehlteKarte();
        for(int i = 0; i<lobby.getTeilnehmerliste().size();i++){
            lobby.getTeilnehmerliste().get(i).setPosition(this.karte.getStartpositionen().get(i));
            this.spielerListe.add(lobby.getTeilnehmerliste().get(i));
        }
        this.startZeitpunkt = new Timestamp(System.currentTimeMillis());
        this.id = lobby.getlobbyID();
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }


    public void setSpielerListe(ArrayList<Spieler> teilnehmerliste) {
        this.spielerListe = teilnehmerliste;
    }

    public String getId(){
        return this.id;
    }


    public Timestamp getStartZeitpunkt() {
        return startZeitpunkt;
    }

    public Karte getKarte() {
        return karte;
    }

}
