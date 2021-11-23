package de.hsrm.mi.swt.rheinmainadventure.model;

public class Spieler {
    private int id;
    private String name;

    public Spieler(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
