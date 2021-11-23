package de.hsrm.mi.swt.rheinmainadventure.messaging;
public class LobbyMessage {
    public static final String NEUER_MITSPIELER = "neuerMitspieler";
    public static final String LOBBYZEIT_ABGELAUFEN = "lobbyzeitAbgelaufen";
    private String operation;
    private String id;

    public LobbyMessage() {
    }

    public LobbyMessage(String op, String id) {
        this.operation = op;
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LobbyMessage [id=" + id + ", operation=" + operation + "]";
    }
}