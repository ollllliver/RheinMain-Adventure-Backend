package de.hsrm.mi.swt.rheinmainadventure.entities;

public enum Interagierbar {
    SCHLUESSEL("Schlüssel"),
    TUER("Tür");

    private final String name;

    Interagierbar(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}  
