package de.hsrm.mi.swt.rheinmainadventure.model;

import java.util.ArrayList;

public class Lobby {
    private String lobbyID;
    private ArrayList<Player> playerList;
    private Player host;

    public Lobby(String lobbyID, ArrayList<Player> playerList, Player host) {
        this.lobbyID = lobbyID;
        this.playerList = playerList;
        this.host = host;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public String getlobbyID() {
        return this.lobbyID;
    }

    public void setlobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public Player getHost() {
        return this.host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

}
