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

    private NachrichtenCode operation;
    private Boolean istFehler;
    private String payload;

    public LobbyMessage() {
    }

    public LobbyMessage(NachrichtenCode op, Boolean istFehler) {
        this.operation = op;
        this.istFehler = istFehler;
        this.payload = "";
    }

    public LobbyMessage(NachrichtenCode op, Boolean istFehler,String payload) {
        this.operation = op;
        this.istFehler = istFehler;
        this.payload = payload;
    }

    public NachrichtenCode getOperation() {
        return operation;
    }

    public void setOperation(NachrichtenCode operation) {
        this.operation = operation;
    }

    public Boolean getIstFehler() {
        return istFehler;
    }

    public void setIstFehler(Boolean istFehler) {
        this.istFehler = istFehler;
    }

    @Override
    public String toString() {
        return "LobbyMessage [istFehler=" + istFehler + ", operation=" + operation + "]";
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

            
}