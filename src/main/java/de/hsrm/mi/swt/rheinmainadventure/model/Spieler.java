package de.hsrm.mi.swt.rheinmainadventure.model;

/**
 * Spielerklasse für die Lobbys. Hat nix mit LogIn zu tun! Ein Spieler wird
 * dann erstellt, wenn er einer Lobby hinzugefügt werden soll. Er nutzt nur
 * quasi zufällig praktischerweise den eingeloggten Nutzernamen des Benutzers,
 * ist aber UNABHÄNGIG vom Benutzer.
 */
public class Spieler {
    private String name;
    private SpielerEigenschaften eigenschaften;
    private boolean isHost;

    public Spieler(String name) {
        this.name = name;
        this.eigenschaften = new SpielerEigenschaften();
    }

    public Spieler() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public SpielerEigenschaften getEigenschaften() {
        return eigenschaften;
    }

    public void setEigenschaften(SpielerEigenschaften eigenschaften) {
        this.eigenschaften = eigenschaften;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Spieler other = (Spieler) obj;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

}
