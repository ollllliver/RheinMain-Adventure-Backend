package de.hsrm.mi.swt.rheinmainadventure.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.model.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Player;

@Service
public class LobbyService {

    ArrayList<Lobby> lobbies = new ArrayList<Lobby>();

    public Lobby createLobby() {
        // TODO random ID generieren
        Player host = new Player(0, "Player1");
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(host);

        Lobby lobby = new Lobby("iwbvüubv+uq", players, host);

        lobbies.add(lobby);

        return lobby;
    }

    public ArrayList<Lobby> getLobbies() {
        return this.lobbies;
    }

    public Lobby getLobbyById(String Id){
        for(Lobby currLobby : lobbies){
            if(currLobby.getlobbyID() == Id){
                return currLobby;
            }
        }
        return null;
    }

}