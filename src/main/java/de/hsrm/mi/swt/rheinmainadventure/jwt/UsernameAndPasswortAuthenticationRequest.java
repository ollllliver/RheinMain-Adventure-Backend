package de.hsrm.mi.swt.rheinmainadventure.jwt;

public class UsernameAndPasswortAuthenticationRequest {

    private String username;
    private String password;

    public UsernameAndPasswortAuthenticationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
