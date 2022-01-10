package de.hsrm.mi.swt.rheinmainadventure.entities;

public enum Mobiliartyp {
    EINGANG(2), AUSGANG(3), SCHLUESSEL(4), NPC(5), TUER(6);

    private int value;
    private Mobiliartyp(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }

}
