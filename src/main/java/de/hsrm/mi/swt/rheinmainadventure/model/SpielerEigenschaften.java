package de.hsrm.mi.swt.rheinmainadventure.model;

public class SpielerEigenschaften {
    private Position position;

    public SpielerEigenschaften(Position position) {
        this.position = position;
    }

    public SpielerEigenschaften() {
        this.position = new Position(0,0,0);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
