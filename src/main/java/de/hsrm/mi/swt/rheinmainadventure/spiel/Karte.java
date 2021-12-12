package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;

import javax.persistence.Tuple;

public class Karte {
    // eine Liste von Startpositionen für Spieler.
    // Die Spieler werden der Reihe nach an den Positionen platziert,
    // bis es keine Spieler mehr gibt.
    private ArrayList<Tuple> startpositionen;

    public ArrayList<Tuple> getStartpositionen() {
        return startpositionen;
    }

    public void setStartpositionen(ArrayList<Tuple> startpositionen) {
        this.startpositionen = startpositionen;
    }
}
