package de.hsrm.mi.swt.rheinmainadventure.model;

import java.util.ArrayList;

public class Lobby {
    private String lobbyID;
    private ArrayList<Player> playerList;
    private Player host;
    private boolean istVoll;
    private boolean istGestartet;
    private int spielerlimit = 0;


    public Lobby(String lobbyID, ArrayList<Player> playerList, Player host) {
        this.lobbyID = lobbyID;
        this.playerList = playerList;
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public boolean getIstVoll(){
        return this.istVoll;
    }

    public void setIstVoll(boolean istVollNeu){
        this.istVoll = istVollNeu;
    }

    public boolean getIstGestartet(){
        return this.istGestartet;
    }
    public void setIstGestartet(boolean istGestartetNeu){
        this.istGestartet = istGestartetNeu;
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

    public int getSpielerlimit() {
        return spielerlimit;
    }

    public void setSpielerlimit(int spielerlimit) {
        this.spielerlimit = spielerlimit;
    }

    

}
