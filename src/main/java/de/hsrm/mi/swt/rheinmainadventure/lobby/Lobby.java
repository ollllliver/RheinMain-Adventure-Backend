package de.hsrm.mi.swt.rheinmainadventure.lobby;

import java.util.ArrayList;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

public class Lobby {
    private String lobbyID;
    private ArrayList<Spieler> teilnehmerliste;
    private ArrayList<Benutzer> benutzerListe;
    private Spieler host;
    private boolean istVoll;
    private boolean istGestartet;
    private boolean istPrivat;
    private int spielerlimit;

    //Aktuellen LobbyService reinreichen lassen da ich nicht weiß wie man bei einer nicht Component Klasse Autowired.
    public Lobby(String lobbyID, ArrayList<Spieler> teilnehmerliste, Spieler host) {
        this.lobbyID = lobbyID;
        this.teilnehmerliste = teilnehmerliste;
        this.benutzerListe = new ArrayList<Benutzer>();
        this.host = host;
        this.istVoll = false;
        this.istGestartet = false;
        this.istPrivat = true;
        this.spielerlimit = 4;
    }

    public void nutzerHinzufuegen(Spieler teilnehmer) {
        // Wenn Lobby nicht voll oder im Spiel (oder Spieler nicht schon drinnen), wird
        // der Spieler in die Teilnehmerliste aufgenommen
        // und es wird gegebenenfalls istVoll angepasst.
        // eventuell hier TODO: ueberpruefen, ob der Spieler bereits in der lobby ist.
        if (!istGestartet && !istVoll) {
            teilnehmerliste.add(teilnehmer);
            istVoll = (teilnehmerliste.size() >= spielerlimit);
        }
    }

    public ArrayList<Spieler> getSpielerList() {
        return teilnehmerliste;
    }

    public void setSpielerList(ArrayList<Spieler> teilnehmerliste) {
        this.teilnehmerliste = teilnehmerliste;
    }

    public boolean getIstVoll(){
        istVoll = (benutzerListe.size()>=spielerlimit);
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

    public ArrayList<Benutzer> getBenutzerListe() {
        return benutzerListe;
    }

    public void setBenutzerListe(ArrayList<Benutzer> benutzerListe) {
        this.benutzerListe = benutzerListe;
    }

    public boolean getIstPrivat() {
        return istPrivat;
    }

    public void setIstPrivat(boolean istPrivat) {
        this.istPrivat = istPrivat;
    }
}
