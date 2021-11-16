package de.hsrm.mi.swt.rheinmainadventure.Lobby.LobbyModel;

import java.util.ArrayList;

public class LobbyManager {

    private ArrayList<Lobby> lobbys = new ArrayList<Lobby>();

    public LobbyManager() {

    }

    public void addLobby(Lobby lobby) {
        this.lobbys.add(lobby);
    }

}
