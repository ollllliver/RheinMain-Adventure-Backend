package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;

public class Spiel {
    private ArrayList<Spieler> teilnehmerliste;

    public Spiel(Lobby lobby) {
        this.teilnehmerliste = lobby.getTeilnehmerliste();
    }


    public ArrayList<Spieler> getTeilnehmerliste() {
        return teilnehmerliste;
    }


    public void setTeilnehmerliste(ArrayList<Spieler> teilnehmerliste) {
        this.teilnehmerliste = teilnehmerliste;
    }
    
}
