package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
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

    private Map<String, Spiel> spielListe = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(SpielServiceImpl.class);

    @Override
    public void starteSpiel(Lobby lobby) {
        spielListe.put(lobby.getlobbyID(), new Spiel(lobby));
    }

    /**
     * Methode zum abrufen aller Spiele
     * 
     * @return alle laufenden Spiele
     */
    @Override
    public List<Spiel> alleSpiele() {
        return new ArrayList<Spiel>(this.spielListe.values());
    }

    /**
     * Methode zum abrufen aller Spieler
     * 
     * @return alle teilnehmenden Spieler
     */
    @Override
    public List<Spieler> getSpielerListeBySpiel(Spiel spiel) {
        return spiel.getSpielerListe();
    }

    /**
     * Methode zum aktualisieren der Spielerposition
     * 
     * @param spieler  übermittelter Spieler, dessen Position aktualisiert werden soll
     * @param position neue Position die an den Spieler übermittelt werden soll
     * @return Spieler mit aktualisierten positionierungs Koordinaten
     */
    @Override
    public Spieler positionsAktualisierung(Spieler spieler, Position position) {
        spieler.getEigenschaften().setPosition(position);
        return spieler;
    }

    @Override
    public void anzahlSchluesselErhoehen(Spieler spieler) {
        spieler.getEigenschaften().schlüssel++;
    }

    @Override
    public void anzahlSchluesselVerringern(Spieler spieler) {
        if (spieler.getEigenschaften().getSchlüssel() >= 0) {
            spieler.getEigenschaften().schlüssel--;
        }
    }

    @Override
    public Spieler getSpieler(String spielID, String name) {
        Spiel spiel = spielListe.get(spielID);

        for (Spieler spieler : getSpielerListeBySpiel(spiel)) {
            // logger.info("SpielerServiceImpl.setSpielerPosition wird aufgerufen");
            if (spieler.getName().equals(name)) {
                return spieler;
            }
        }
        return null; // throw SpielerNotFoundException
    }

}
