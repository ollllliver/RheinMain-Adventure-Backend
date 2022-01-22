package de.hsrm.mi.swt.rheinmainadventure.model;

/**
 * Modell einer Chat-Nachricht, welche zwischen dem Client und dem Server ausgetauscht wird.
 * Beinhaltet den Typ einer Nachricht (Join-Nachricht / Leave-Nachricht / normale Text-Nachricht),
 * den eigentlichen Inhalt der Nachricht (wenn es sich um eine Join/Leave-Nachricht handelt ist der Inhalt leer)
 * und den Namen des Senders, der die Nachricht verschickt hat.
 */
public class ChatNachricht {
    private NachrichtenTyp typ;
    private String inhalt;
    private String sender;

    public ChatNachricht(NachrichtenTyp typ, String inhalt, String sender) {
        this.typ = typ;
        this.inhalt = inhalt;
        this.sender = sender;
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

    /**
     * Kennzeichnet die Art der Chat-Nachricht. Hier wird unterschieden zwischen
     * Join-Nachricht, Leave-Nachricht und einer normalen Text-Nachricht.
     * Wenn es sich um eine Join- oder Leave-Nachricht handelt ist der Inhalt der Nachricht leer.
     */
    public enum NachrichtenTyp {
        CHAT,
        JOIN,
        LEAVE
    }
}