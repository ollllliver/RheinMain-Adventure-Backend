package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Rest Controller für /api/level/*
 * <p>
 * Der LevelRestController bietet die meissten Funktionen des LevelServices bequem per REST-API an, sodass das Frontend
 * komfortabel mit der Datenhaltung in der Datenbank interagieren kann.
 */
@CrossOrigin
@RestController
public class LevelRestController {

    public static final String LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE = "Das Level existiert in der DB, jetzt wird der Raum geholt";
    public static final String LEVEL_NICHT_IN_DB_404_LOG_MESSAGE = "Level nicht in DB gefunden, externer Aufrufer erhält 404";
    public static final EntityNichtInDatenbankException LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION = new EntityNichtInDatenbankException("Das Level gibt es nicht in der Datenbank");
    private final Logger lg = LoggerFactory.getLogger(LevelRestController.class);

    @Autowired
    private LevelService levelService;

    /**
     * @return Eine Liste aller in der DB gespeicherten Level
     */
    @GetMapping(value = "/api/level/alle")
    public List<Level> getAlleLevel() {
        return levelService.alleLevel();
    }


    /**
     * Erstellt für den Nutzer 'benutzername' ein neues, leeres Level und befüllt es direkt rudimentär mit Werten.
     * Bislang können Level nur erstellt werden, nicht bearbeitet
     *
     * @param benutzername   ist ein in der DB vorhandener Nutzername, über den Level und Benutzer verknüpft werden.
     * @param levelParameter Eine Map, die die Schlüssel "name" und "beschreibung" enthalten muss, um diese Werte beim Level zu setzen
     * @return Ein mit der Datenbank verknüpftes Level, das als JSON zurückgegeben wird
     */
    @PostMapping(value = "/api/level/neu/{benutzername}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Level neuesLevel(@PathVariable String benutzername, Map<String, String> levelParameter) {
        // Nicht sonderlich sauber, jedoch für UserStory ausreichend
        return levelService.bearbeiteLevel(
                benutzername,
                new Level(levelParameter.get("name"), levelParameter.get("beschreibung"), (byte) 0, new ArrayList<>())
        );
    }


    /**
     * Löscht ein Level über eine gegebene Level-ID. Das löscht übrigens auch alle Räume des Levels.
     *
     * @param levelID ist die Level-ID, die in der DB existieren sollte
     * @see LevelServiceImpl#loescheLevel
     */
    @DeleteMapping("/api/level/{levelID}")
    public void deleteLevel(@PathVariable long levelID) {
        try {
            levelService.loescheLevel(levelID);
        } catch (NoSuchElementException e) {
            throw new EntityNichtInDatenbankException("Es gibt kein Level, das man löschen könnte");
        }
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
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);
            Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
            lg.info("Raumindex gibt es auch, Rauminhalt wird über JSON versendet");
            return angefragterRaum.getRaumMobiliar();
        }
        lg.warn(LEVEL_NICHT_IN_DB_404_LOG_MESSAGE);
        throw LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION;
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
    public Position getStartPositionImRaum(@PathVariable long levelID, @PathVariable int raumindex) {
        lg.info("Startposition von Level ID {} und Raumindex {} über REST angefragt", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);

        if (angefragtesLevel.isPresent()) {
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);

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
        lg.warn(LEVEL_NICHT_IN_DB_404_LOG_MESSAGE);
        throw LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION;
    }

    /**
     * Liefert mit einem GET-Aufruf den Inhalt eines Levels in Form eines 2D-Arrays, das an dan passenden Stellen
     * im Array die Mobiliar-IDs enthält.
     *
     * @param levelID   Die Level-Id, die in der DB angefragt werden soll. Teil der Request-URL.
     * @param raumindex Der Raum-Index aus dem gesuchten Level.
     * @return Eine Liste an RaumMobiliar-Objekten, über die man das Mobiliar und seine Position erhält.
     */
    @GetMapping(value = "/api/level/einfach/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public long[][] getEinfachenRauminhalt(@PathVariable long levelID, @PathVariable int raumindex) {
        lg.info("Einfacher Rauminhalt von Level ID {} und Raumindex {} über REST angefragt", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);
        if (angefragtesLevel.isPresent()) {
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);

            try {
                // Wenn der RaumIndex zu hoch ist, wirft der LevelService eine NoSuchElementException, die wir fangen sollten.

                Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
                lg.info("Raumindex gibt es auch, jetzt wird das Array befüllt");

                // Jetzt holen wir uns das gesamte RaumMobiliar des Raumes...
                List<RaumMobiliar> raumMobiliar = angefragterRaum.getRaumMobiliar();
                long[][] einfacherRaumInhalt = new long[14][22];
                for (RaumMobiliar r : raumMobiliar) {
                    // ... und fügen die Mobiliar-ID an der dazugehörigen X/Y-Stelle im Array ein
                    einfacherRaumInhalt[r.getPositionX()][r.getPositionY()] = r.getMobiliar().getMobiliarId();
                }

                // Es kann jetzt noch sein, dass manche Stellen im Array nicht befüllt sind. Das muss man entweder im
                // Frontend abfangen, oder wir füllen die leeren stellen einfach hier noch mit Wänden oder so

                return einfacherRaumInhalt;

            } catch (NoSuchElementException e) {
                // Wenn es den Raum also noch nicht gibt, geben wir einfach einen mit Wänden (ID 0) gefüllten zurück.

                long[][] einfacherRaumInhalt = new long[14][22];
                for (long[] yAchse : einfacherRaumInhalt) {
                    Arrays.fill(yAchse, 0L);
                }

                return einfacherRaumInhalt;
            }

        }
        lg.warn(LEVEL_NICHT_IN_DB_404_LOG_MESSAGE);
        throw LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION;
    }


    @PutMapping(value = "/api/level/einfach/{levelID}/{raumindex}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Raum putEinfachenRauminhalt(@PathVariable long levelID, @PathVariable int raumindex, long[][] einfacherRaumInhalt) {
        lg.info("Einfachen Rauminhalt zu Level ID {} und Raumindex {} über REST erhalten", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);
        if (angefragtesLevel.isPresent()) {
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);

            try {
                // Wenn der RaumIndex zu hoch ist, wirft der LevelService eine NoSuchElementException, die wir fangen sollten.

                Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
                lg.info("Raumindex gibt es auch, jetzt wird das Array befüllt");

                // Wir machen uns eine neue leere Liste...
                List<RaumMobiliar> neuesRaumMobiliar = new ArrayList<>();
                for (int x = 0; x < einfacherRaumInhalt.length; x++) {
                    for (int y = 0; y < einfacherRaumInhalt[x].length; y++) {
                        // iterieren über den externen Rauminhalt und mappen ihn auf RaumMobiliar-Objekte der DB
                        neuesRaumMobiliar.add(new RaumMobiliar(
                                levelService.getMobiliar(einfacherRaumInhalt[x][y]),
                                angefragterRaum,
                                x,
                                y)
                        );
                    }
                }
                // Jetzt haben wir einen korrekt befüllten Raum, den werfen wir jetzt noch in das Level rein
                angefragterRaum.setRaumMobiliar(neuesRaumMobiliar);

                // Da der Spaß transactional ist, sollte es das vielleicht schon gewesen sein. Ansonsten einfach nochmal
                // bearbeiteLevel() aufrufen

                return angefragterRaum;

            } catch (NoSuchElementException e) {
                // Wenn es den Raum also noch nicht gibt, erstellen wir einen neuen im Level.
                // Wir machen uns einen neuen Raum mit frischem RaumMobiliar
                List<RaumMobiliar> neuesRaumMobiliar = new ArrayList<>();
                Raum angefragterRaum = new Raum(raumindex, neuesRaumMobiliar);

                // und befüllen das RaumMobiliar
                for (int x = 0; x < einfacherRaumInhalt.length; x++) {
                    for (int y = 0; y < einfacherRaumInhalt[x].length; y++) {
                        // iterieren über den externen Rauminhalt und mappen ihn auf RaumMobiliar-Objekte der DB
                        neuesRaumMobiliar.add(new RaumMobiliar(
                                levelService.getMobiliar(einfacherRaumInhalt[x][y]),
                                angefragterRaum,
                                x,
                                y)
                        );
                    }
                }
                angefragtesLevel.get().getRaeume().add(angefragterRaum);
                return angefragterRaum;
            }
        }
        lg.warn(LEVEL_NICHT_IN_DB_404_LOG_MESSAGE);
        throw LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION;
    }


}
