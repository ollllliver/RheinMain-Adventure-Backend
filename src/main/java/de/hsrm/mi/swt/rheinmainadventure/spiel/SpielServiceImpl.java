package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.model.SpielerStatus;

@Service
public class SpielServiceImpl implements SpielService {

    private Map<String, Spiel> spielListe;

    Logger logger = LoggerFactory.getLogger(SpielServiceImpl.class);


    @Override
    public void starteSpiel(Lobby lobby) {
        spielListe.put(lobby.getlobbyID(), new Spiel(lobby));
    }

    /**
     * Methode zum abrufen aller Spiele
     * @return alle laufenden Spiele
     */
    @Override
    public List<Spiel> alleSpiele() {
        return new ArrayList<Spiel>(this.spielListe.values());
    }

    /**
     * Methode zum abrufen aller Spieler
     * @return alle teilnehmenden Spieler
     */
    @Override
    public List<Spieler> alleSpieler(String spielID) {
        ArrayList<Spieler> spielerListe = new ArrayList<Spieler>();
        spielerListe = spielListe.get(spielID).getSpielerListe();
        return spielerListe;
    }

    /**
     * Methode zum aktualisieren der Spielerposition
     * @param spieler übermittelter Spieler, dessen Position aktualisiert werrden soll
     * @param position neue Position die an den Spieler übermittelt werden soll
     * @return Spieler mit aktualisierten positionierungs Koordinaten
     */
    @Override
    public Spieler positionsAktualisierung(Spieler spieler, Position position) {
        spieler.getEigenschaften().setPosition(position);
        // TODO: Sende Spieler Position über Stomp
        return spieler;
    }

    @Override
    public Spieler statusAktualisierung(Spieler spieler, SpielerStatus status) {
        for (SpielerStatus spielerStatus : spieler.getEigenschaften().getStatusListe()) {
            // TODO: SpielerStatus ändern
        }
        return spieler;
    }

    @Override
    public void setSpielerPosition(String lobbyID, String name, Position position){
        Spiel spiel = getSpielByLobbyId(lobbyID);

        for ( Spieler spieler : getSpielerListeBySpiel(spiel)) {
            //logger.info("SpielerServiceImpl.setSpielerPosition  wird aufgerufen");
            if (spieler.getName().equals(name)) {
                spieler.getEigenschaften().setPosition(position);
            }   
        }
        updateSpielerPositionen(lobbyID);
    }

    @Override
    public List<Spieler> getSpielerListeBySpiel(Spiel spiel) {
        return spiel.getSpielerListe();
    }

    public void updateSpielerPositionen(String lobbyID){ //TODO
        //broker.convertAndSend("/topic/spiel" + lobbyID, getSpielerListeByLobbyId(lobbyID));
    }

    @Override
    public Spiel getSpielByLobbyId(String id) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
