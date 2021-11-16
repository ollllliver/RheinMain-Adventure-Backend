package de.hsrm.mi.swt.rheinmainadventure.Lobby.LobbyModel;

import java.util.ArrayList;

import de.hsrm.mi.swt.rheinmainadventure.entities.*;

public class Lobby {
    /**
     * Erstmal den DUmmy benutzer genommen
     * 
     */
    private String lobbyID = "";
    private ArrayList<Benutzer> benutzerList = new ArrayList<Benutzer>();
    private Benutzer host = new Benutzer();

    public Lobby() {
    }

    public Lobby(String lobbyID, Benutzer host) {
        this.lobbyID = lobbyID;
        benutzerList.add(host);
        this.host = host;

    }

    public void setlobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public ArrayList<Benutzer> getBenutzerList() {
        return benutzerList;
    }

    public void setBenutzerList(ArrayList<Benutzer> benutzerList) {
        this.benutzerList = benutzerList;
    }

    public void setHost(Benutzer host) {
        this.host = host;
    }

    public String getlobbyID() {
        return this.lobbyID;
    }

    public ArrayList<Benutzer> getbenutzerList() {
        return this.benutzerList;
    }

    public Benutzer getHost() {
        return this.host;
    }

}
