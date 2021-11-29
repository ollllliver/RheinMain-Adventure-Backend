package de.hsrm.mi.swt.rheinmainadventure.messaging;
public class LobbyMessage {
    
    private NachrichtenCode operation;
    private Boolean istFehler;
    
    public LobbyMessage() {
    }

    public LobbyMessage(NachrichtenCode op, Boolean istFehler) {
        this.operation = op;
        this.istFehler = istFehler;
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


}