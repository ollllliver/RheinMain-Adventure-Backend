package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;

import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public class Lobby {
    private String lobbyID;
    private ArrayList<Spieler> teilnehmerliste;
    private Spieler host;
    private boolean istVoll;
    private boolean istGestartet;
    private boolean istPrivat;
    private int spielerlimit;

    public Lobby(String lobbyID, ArrayList<Spieler> teilnehmerliste, Spieler host) {
        this.lobbyID = lobbyID;
        this.teilnehmerliste = teilnehmerliste;
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
        this.istPrivat = true;
        this.spielerlimit = 2;
    }
    
    public Lobby() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + (istGestartet ? 1231 : 1237);
        result = prime * result + (istPrivat ? 1231 : 1237);
        result = prime * result + (istVoll ? 1231 : 1237);
        result = prime * result + ((lobbyID == null) ? 0 : lobbyID.hashCode());
        result = prime * result + spielerlimit;
        result = prime * result + ((teilnehmerliste == null) ? 0 : teilnehmerliste.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Lobby other = (Lobby) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (istGestartet != other.istGestartet)
            return false;
        if (istPrivat != other.istPrivat)
            return false;
        if (istVoll != other.istVoll)
            return false;
        if (lobbyID == null) {
            if (other.lobbyID != null)
                return false;
        } else if (!lobbyID.equals(other.lobbyID))
            return false;
        if (spielerlimit != other.spielerlimit)
            return false;
        if (teilnehmerliste == null) {
            if (other.teilnehmerliste != null)
                return false;
        } else if (!teilnehmerliste.equals(other.teilnehmerliste))
            return false;
        return true;
    }

    public ArrayList<Spieler> getTeilnehmerliste() {
        return teilnehmerliste;
    }

    public void setTeilnehmerliste(ArrayList<Spieler> teilnehmerliste) {
        this.teilnehmerliste = teilnehmerliste;
    }

    public boolean getIstVoll(){
        istVoll = (teilnehmerliste.size()>=spielerlimit);
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

    public Spieler getHost() {
        return this.host;
    }

    public void setHost(Spieler host) {
        this.host = host;
    }

    public int getSpielerlimit() {
        return spielerlimit;
    }

    public void setSpielerlimit(int spielerlimit) {
        this.spielerlimit = spielerlimit;
    }

    public boolean getIstPrivat() {
        return istPrivat;
    }

    public void setIstPrivat(boolean istPrivat) {
        this.istPrivat = istPrivat;
    }
}
