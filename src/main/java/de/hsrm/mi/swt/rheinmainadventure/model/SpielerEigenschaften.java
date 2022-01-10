package de.hsrm.mi.swt.rheinmainadventure.model;

public class SpielerEigenschaften {
    private Position position;
    public int schlüssel;

    public SpielerEigenschaften(Position position) {
        this.position = position;
        this.schlüssel = 0;
    }

    public SpielerEigenschaften() {
        this.position = new Position(0,0,0);
        this.schlüssel = 0;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getSchlüssel() {
        return schlüssel;
    }

    public void setSchlüssel(int schlüssel) {
        this.schlüssel = schlüssel;
    }

}
