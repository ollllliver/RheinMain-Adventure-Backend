package de.hsrm.mi.swt.rheinmainadventure.messaging;

/**
 * Klasse zur Vereinheitlichung der Kommunikation zum Frontend.
 * 
 * SpielMessages werden sowohl über Rest als auch über Stomp zur Kommunikation
 * verwendet. Eine SpielMessage beinhaltet einen Nachrichtencode und...
 * Ein nachrichtencode ist ein Enum, um Schreibfehler zu vermeiden. Bei
 * änderungen der Nachrichtencodes bitte auch daran denken, den Nachrichtencode
 * im Frontend zu ändern!
 */
public class SpielMessage {

    private NachrichtenCode typ;
    private Boolean istFehler;
    private String payload;

    public SpielMessage() {
    }

    public SpielMessage(NachrichtenCode op, Boolean istFehler) {
        this.typ = op;
        this.istFehler = istFehler;
        this.payload = "";
    }

    public SpielMessage(NachrichtenCode op, Boolean istFehler,String payload) {
        this.typ = op;
        this.istFehler = istFehler;
        this.payload = payload;
    }

    public NachrichtenCode getTyp() {
        return typ;
    }

    public void setTyp(NachrichtenCode typ) {
        this.typ = typ;
    }

    public Boolean getIstFehler() {
        return istFehler;
    }

    public void setIstFehler(Boolean istFehler) {
        this.istFehler = istFehler;
    }

    @Override
    public String toString() {
        return "SpielMessage [istFehler=" + istFehler + ", typ=" + typ + "]";
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

            
}