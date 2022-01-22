package de.hsrm.mi.swt.rheinmainadventure.oldJwt;

/**
 * JwtLoginRequest - einfache Java-Datenklasse, um vom
 * Vue-Frontend (doLogin()) gelieferte JSON-Struktur
 * { 'username': '...', 'password': '...' }
 * aufzunehmen.
 */
public class JwtLoginRequest {
    private String benutzername;
    private String passwort;
    
    public String getBenutzername() {
        return benutzername;
    }
    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }
    public String getPasswort() {
        return passwort;
    }
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }
    @Override
    public String toString() {
        return "JwtLoginRequest [password=" + passwort + ", username=" + benutzername + "]";
    }
}
