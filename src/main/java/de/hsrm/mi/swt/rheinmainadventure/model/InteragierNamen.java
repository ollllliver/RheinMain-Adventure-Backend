package de.hsrm.mi.swt.rheinmainadventure.model;

public enum InteragierNamen {
    TUER("Tür"),
    SCHLUESSSEL("Schlüssel");

    private final String name;

    private InteragierNamen(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }


    
}
