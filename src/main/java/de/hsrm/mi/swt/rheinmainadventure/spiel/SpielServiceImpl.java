package de.hsrm.mi.swt.rheinmainadventure.spiel;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;

@Service
public class SpielServiceImpl implements SpielService {

    @Autowired
    LevelService levelService;

    private Map<String, Spiel> spielListe = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(SpielServiceImpl.class);

    /**
     * Methode welche beim Spielstart die Spiel-ID angibt und die Startpositionen der teilnehmenden Spieler festlegt
     * 
     * @param lobby die Lobby mit den teilnehmenden Spielern, zu welcher das Spiel gestartet werden soll
     */
    @Override
    public void starteSpiel(Lobby lobby) {
        
        List<Spieler> spielerListe = new ArrayList<Spieler>();
        Position startposition = levelService.getStartPositionImRaum(levelService.getRaum(lobby.getGewaehlteKarte(), 0));

        for (int i = 0; i < lobby.getTeilnehmerliste().size(); i++) {
            lobby.getTeilnehmerliste().get(i).getEigenschaften().setPosition(startposition);
            spielerListe.add(lobby.getTeilnehmerliste().get(i));
        }

        spielListe.put(lobby.getlobbyID(), new Spiel(lobby, spielerListe));
        logger.info(spielListe.toString());
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
    public List<Spieler> getSpielerListeZumSpiel(Spiel spiel) {
        return spiel.getSpielerListe();
    }

    /**
     * Methode zum aktualisieren der Spielerposition
     * 
     * @param spieler übermittelter Spieler, dessen Position aktualisiert werden soll
     * @param position neue Position die an den Spieler übermittelt werden soll
     * @return Spieler mit aktualisierten positionierungs Koordinaten
     */
    @Override
    public Spieler positionsAktualisierung(Spieler spieler, Position position) {
        spieler.getEigenschaften().setPosition(position);
        return spieler;
    }

    /**
     * Methode welche den ausgwählten Spieler zurück gibt
     * 
     * @param spielID die id des Spielers, welcher zurück gegeben werden soll
     * @param name der Name des Spielers
     * @return den ausgewählten Spieler
     */
    @Override
    public Spieler getSpieler(String spielID, String name) {
        Spiel spiel = spielListe.get(spielID);

        for (Spieler spieler : getSpielerListeZumSpiel(spiel)) {
            // logger.info("SpielerServiceImpl.setSpielerPosition wird aufgerufen");
            if (spieler.getName().equals(name)) {
                return spieler;
            }
        }
        return null; // throw SpielerNotFoundException
    }

    /**
     * Methode zum erhöhen der Schlüsselanzahl im Spiel
     * 
     * @param spiel aktuelles Spiel, in welchem der Zähler der gefundenen Schlüssel erhöht wird
     * @return gibt die aktuelle Anzahl an gefundenen Schlüsseln zurück
     */
    @Override
    public int anzahlSchluesselErhoehen(Spiel spiel) {
        spiel.setAnzSchlüssel(spiel.getAnzSchlüssel() + 1);
        return spiel.getAnzSchlüssel();
    }

    /**
     * Methode zum verringern der Schlüsselanzahl im Spiel
     * 
     * @param spiel aktuelles Spiel, in welchem der Zähler der gefundenen Schlüssel verringert wird
     * @return gibt die aktuelle Anzahl an gefundenen Schlüsseln zurück
     */
    @Override
    public int anzahlSchluesselVerringern(Spiel spiel) {
        if (spiel.getAnzSchlüssel() >= 0) {
            spiel.setAnzSchlüssel(spiel.getAnzSchlüssel() - 1);
        }
        return spiel.getAnzSchlüssel();
    }

}
