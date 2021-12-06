package de.hsrm.mi.swt.rheinmainadventure.model;

public class ChatNachricht {
    private NachrichtenTyp typ;
    private String inhalt;
    private String sender;

    public enum NachrichtenTyp {
        CHAT,
        JOIN,
        LEAVE
    }

    public NachrichtenTyp getTyp() {
        return typ;
    }

    public void setTyp(NachrichtenTyp typ) {
        this.typ = typ;
    }

    public String getInhalt() {
        return inhalt;
    }

    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}