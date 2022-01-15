package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Rest Controller für /api/level/*
 * <p>
 * Der LevelRestController bietet die meissten Funktionen des LevelServices bequem per REST-API an, sodass das Frontend
 * komfortabel mit der Datenhaltung in der Datenbank interagieren kann.
 */
@CrossOrigin
@RestController
public class LevelRestController {

    private final Logger lg = LoggerFactory.getLogger(LevelRestController.class);
    @Autowired
    private LevelService levelService;
    @Autowired
    private MobiliarRepository mobiliarRepository;

    /**
     * @return Eine Liste aller in der DB gespeicherten Level
     */
    @GetMapping(value = "/api/level/alle")
    public List<Level> getAlleLevel() {
        return levelService.alleLevel();
    }

    /**
     * Liefert mit einem GET-Aufruf eine Liste an RaumMobiliar zu einem gegebenen Raum eines Levels.
     *
     * @param levelID   Die Level-Id, die in der DB angefragt werden soll. Teil der Request-URL.
     * @param raumindex Der Raum-Index aus dem gesuchten Level.
     * @return Eine Liste an RaumMobiliar-Objekten, über die man das Mobiliar und seine Position erhält.
     */
    @GetMapping(value = "/api/level/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RaumMobiliar> getRauminhalt(@PathVariable long levelID, @PathVariable int raumindex) {
        lg.info("Rauminhalt von Level ID {} und Raumindex {} über REST angefragt", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);
        if (angefragtesLevel.isPresent()) {
            lg.info("Das Level existiert in der DB, jetzt wird der Raum geholt");
            Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
            lg.info("Raumindex gibt es auch, Rauminhalt wird über JSON versendet");
            return angefragterRaum.getRaumMobiliar();
        }
        lg.warn("Level nicht in DB gefunden, externer Aufrufer erhält 404");
        throw new EntityNichtInDatenbankException("Das Level gibt es nicht in der Datenbank");
    }

    /**
     * Gibt über eine Mobiliar-ID die zugehörige .gltf-Datei aus
     *
     * @param mobiliarID ist die Mobiliar-ID, zu der die gltf-Datei heruntergeladen werden soll
     * @return Die gewünschte gltf-Datei (als Datei).
     */
    @GetMapping(value = "/api/level/{mobiliarID}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource getGLTFObject(@PathVariable long mobiliarID) {
        lg.info("GET-Anfrage für die gltf-Datei von Mobiliar-ID {}", mobiliarID);
        try {
            String resourcePath = levelService.getMobiliar3DModellURI(mobiliarID);
            return new FileSystemResource(("src/main/resources/" + resourcePath));
        } catch (EntityNotFoundException e) {
            throw new EntityNichtInDatenbankException("Das Mobiliar gibt es nicht in der Datenbank, oder es hat kein 3D Modell");
        }
    }


    /**
     * Gibt zu einem gesuchten Raum die Startposition als String zurück.
     *
     * @param levelID   Die Level-Id, die in der DB angefragt werden soll.
     * @param raumindex Der Raum-Index aus dem gesuchten Level.
     * @return Die Startposition als String, formatiert durch Position.toString()
     * @see de.hsrm.mi.swt.rheinmainadventure.model.Position
     */
    @GetMapping(value = "/api/level/startposition/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Position startPositionImRaum(@PathVariable long levelID, @PathVariable int raumindex) {
        lg.info("Startposition von Level ID {} und Raumindex {} über REST angefragt", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);

        if (angefragtesLevel.isPresent()) {
            lg.info("Das Level existiert in der DB, jetzt wird der Raum geholt");

            try {
                // Wenn der RaumIndex zu hoch ist, wirft der LevelService eine NoSuchElementException, die wir fangen
                // und in eine EntityNichtInDatenbankException umwandeln müssen

                Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
                lg.info("Raumindex gibt es auch, Rauminhalt wird über JSON versendet");
                return levelService.getStartPositionImRaum(angefragterRaum);

            } catch (NoSuchElementException e) {
                lg.warn("levelService.getRaum lieferte NoSuchElementException, breche mit Error 400 ab. " +
                        "Vielleicht war der RaumIndex zu hoch?");
                throw new LevelAttributZugriffsException("Das Level gab es in der Datenbank, aber den Raum nicht.");
            }
        }
        lg.warn("Level nicht in DB gefunden, externer Aufrufer erhält 404");
        throw new EntityNichtInDatenbankException("Das Level gibt es nicht in der Datenbank");
    }


    // Gehört zu Task 120, später wieder einkommentieren, verbessern & testen

//    @PutMapping(value = "/api/level/speichern", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String speichereLevel(@RequestBody Map<String, ?> levelData) throws NoSuchFieldException {
//        // TODO: JSON aus dem Frontend nochmal analysieren und dann mit einem JSON-Objekt direkt passend mappen.
//
//        List<List<Object>> karte = (List<List<Object>>) levelData.get("karte");
//
//        String levelName = levelData.get("name").toString();
//        int minSpieler = Integer.parseInt(levelData.get("minSpieler").toString());
//        int maxSpieler = Integer.parseInt(levelData.get("maxSpieler").toString());
//        // levelService.levelHinzufuegen(levelName, minSpieler, maxSpieler, (byte) 17, karte);
//
//        //logger.info(levelData[13][5].toString());
//
//        return levelData.toString();
//    }

    // TODO: DELETE-Mapping

}
