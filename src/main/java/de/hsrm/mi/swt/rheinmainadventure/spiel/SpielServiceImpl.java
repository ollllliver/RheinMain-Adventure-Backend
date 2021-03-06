package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.lobby.Lobby;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpielServiceImpl implements SpielService {

    private final Map<String, Spiel> spielListe = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(SpielServiceImpl.class);
    @Autowired
    private LevelService levelService;

    /**
     * Methode welche beim Spielstart die Spiel-ID angibt und die Startpositionen der teilnehmenden Spieler festlegt
     *
     * @param lobby die Lobby mit den teilnehmenden Spielern, zu welcher das Spiel gestartet werden soll
     */
    @Override
    public void starteSpiel(Lobby lobby) {

        List<Spieler> spielerListe = new ArrayList<>();
        Position startposition = levelService
                .getStartPositionImRaum(levelService.getRaum(lobby.getGewaehlteKarte(), 0));

        for (int i = 0; i < lobby.getTeilnehmerliste().size(); i++) {
            lobby.getTeilnehmerliste().get(i).getEigenschaften().setPosition(startposition);
            spielerListe.add(lobby.getTeilnehmerliste().get(i));
        }

        spielListe.put(lobby.getlobbyID(), new Spiel(lobby, spielerListe));
        logger.info("{}", spielListe);
    }

    /**
     * Methode zum abrufen aller Spiele
     *
     * @return alle laufenden Spiele
     */
    @Override
    public List<Spiel> alleSpiele() {
        return new ArrayList<>(this.spielListe.values());
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
     * Methode zum Abrufen des gesuchten Spiels, mittels LobbyId
     *
     * @param lobbyId die id der Lobby an welcher das Spiel gekn??pft ist
     * @return das an die passende Lobby gekn??pfte Spiel
     */
    @Override
    public Spiel getSpielByLobbyId(String lobbyId) {
        return spielListe.get(lobbyId);
    }

    /**
     * Methode zum Aktualisieren der Spielerposition
     *
     * @param spieler  ??bermittelter Spieler, dessen Position aktualisiert werden soll
     * @param position neue Position, die an den Spieler ??bermittelt werden soll
     * @return Spieler mit aktualisierten Positionierungs-Koordinaten
     */
    @Override
    public Spieler positionsAktualisierung(Spieler spieler, Position position) {
        spieler.getEigenschaften().setPosition(position);
        return spieler;
    }

    /**
     * Methode welche den ausgew??hlten Spieler zur??ckgibt
     *
     * @param spielID die id des Spielers, welcher zur??ckgegeben werden soll
     * @param name    der Name des Spielers
     * @return den ausgew??hlten Spieler
     */
    @Override
    public Spieler getSpieler(String spielID, String name) {
        Spiel spiel = spielListe.get(spielID);

        for (Spieler spieler : getSpielerListeBySpiel(spiel)) {
            if (spieler.getName().equals(name)) {
                return spieler;
            }
        }
        return null; // throw SpielerNotFoundException
    }

    /**
     * Methode zum Erh??hen der Schl??sselanzahl im Spiel
     *
     * @param spiel aktuelles Spiel, in welchem der Z??hler der gefundenen Schl??ssel erh??ht wird
     * @return gibt die aktuelle Anzahl an gefundenen Schl??sseln zur??ck
     */
    @Override
    public int anzahlSchluesselErhoehen(Spiel spiel) {
        spiel.setAnzSchluessel(spiel.getAnzSchluessel() + 1);
        return spiel.getAnzSchluessel();
    }

    /**
     * Methode zum verringern der Schl??sselanzahl im Spiel
     *
     * @param spiel aktuelles Spiel, in welchem der Z??hler der gefundenen Schl??ssel verringert wird
     * @return gibt die aktuelle Anzahl an gefundenen Schl??sseln zur??ck
     */
    @Override
    public int anzahlSchluesselVerringern(Spiel spiel) {
        if (spiel.getAnzSchluessel() >= 0) {
            spiel.setAnzSchluessel(spiel.getAnzSchluessel() - 1);
        }
        return spiel.getAnzSchluessel();
    }

    /**
     * Methode zum Finden eines Spiels
     *
     * @param lobbyID da lobbyID = spielID
     * @return das gew??nschte Spiel
     */

    @Override
    public Spiel findeSpiel(String lobbyID) {
        for (Spiel spiel : alleSpiele()) {
            if (spiel.getSpielID().equals(lobbyID)) {
                return spiel;
            }
        }
        return null;
    }

    /**
     * Methode um den Score eines bestimmten Spielers zu erhoehen
     */
    @Override
    public int scoreErhoehen(Spieler spieler, int score) {
        spieler.setScore(score);
        return spieler.getScore();
    }

    /**
     * Methode um den Score eines Spielers zu bekommen
     */
    @Override
    public int spielerScore(Spieler spieler) {
        return spieler.getScore();
    }

}
