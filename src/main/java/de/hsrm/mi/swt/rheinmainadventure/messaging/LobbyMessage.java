package de.hsrm.mi.swt.rheinmainadventure.messaging;

/**
 * Klasse zur Vereinheitlichung der Kommunikation zum Frontend.
 * 
 * LobbyMessages werden sowohl über Rest als auch über Stomp zur Kommunikation
 * verwendet. Eine LobbyMessage beinhaltet einen Nachrichtencode und eine Fehler
 * Flag. Ein nachrichtencode ist ein Enum, um Schreibfehler zu vermeiden. Bei
 * änderungen der Nachrichtencodes bitte auch daran denken, den Nachrichtencode
 * im Frontend zu ändern!
 */
public class LobbyMessage {

    private NachrichtenCode typ;
    private Boolean istFehler;
    private String payload;

    public LobbyMessage() {
    }

    public LobbyMessage(NachrichtenCode typ, Boolean istFehler) {
        this.typ = typ;
        this.istFehler = istFehler;
        this.payload = "";
    }

    public LobbyMessage(NachrichtenCode typ, Boolean istFehler,String payload) {
        this.typ = typ;
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
        return "LobbyMessage [istFehler=" + istFehler + ", typ=" + typ + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((istFehler == null) ? 0 : istFehler.hashCode());
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        result = prime * result + ((typ == null) ? 0 : typ.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LobbyMessage other = (LobbyMessage) obj;
        if (istFehler == null) {
            if (other.istFehler != null)
                return false;
        } else if (!istFehler.equals(other.istFehler))
            return false;
        if (payload == null) {
            if (other.payload != null)
                return false;
        } else if (!payload.equals(other.payload))
            return false;
        if (typ != other.typ)
            return false;
        return true;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

            
}