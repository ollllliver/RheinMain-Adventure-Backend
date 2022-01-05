package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

/**
 * Spiel Service für das verwalten aller Spiele.
 * 
 */
@Service
public class SpielServiceImpl implements SpielService{
    Logger logger = LoggerFactory.getLogger(SpielServiceImpl.class);

    /**
     * Der Messagebroker wird hier durch dependencyInjection eingebunden. Über ihn
     * koennen Nachrichten ueber STOMP an die Subscriber gesendet werden
     */
    @Autowired
    SimpMessagingTemplate broker;
    
    /**
     * Liste aller Spielinstanzen.
     */
    ArrayList<Spiel> spiele = new ArrayList<Spiel>();

    @Override
    public Spiel spielErstellen(Lobby lobby) {
            Spiel spiel = new Spiel(lobby);
            spiele.add(spiel);
        return spiel;
    }

    @Override
    public List<Spiel> getSpiele() {
        return spiele;
    }

    @Override
    public List<Spieler> getSpielerListeByLobbyId(String id) {
        Spiel spiel = getSpielByLobbyId(id);
        return spiel.getSpielerListe();
    }

    public void updateSpielerPositionen(String id){ //TODO
        broker.convertAndSend("/topic/spiel/" + id, getSpielerListeByLobbyId(id));
    }

    @Override
    public Spiel getSpielByLobbyId(String id) {
        for (Spiel currSpiel : spiele) {
            if (currSpiel.getId().equals(id)) {
              return currSpiel;
            }
        }
        return null;
    }

    @Override
    public void setSpielerPosition(String id, String name, Tuple position){
        for ( Spieler spieler : getSpielerListeByLobbyId(id)) {
            if (spieler.getName().equals(name)) {
                spieler.setPosition(position);
            }
        }
    }
    
}
