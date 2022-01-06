package de.hsrm.mi.swt.rheinmainadventure.model;

import java.util.ArrayList;

public class SpielerEigenschaften {
    private Position position;
    private ArrayList<SpielerStatus> statusListe;

    public SpielerEigenschaften(Position position) {
        this.position = position;
    }

    public SpielerEigenschaften() {

    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ArrayList<SpielerStatus> getStatusListe() {
        return statusListe;
    }

    public void setStatusListe(ArrayList<SpielerStatus> statusListe) {
        this.statusListe = statusListe;
    }

}
