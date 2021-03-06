package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.RaumMobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Rest Controller für /api/level/*
 * <p>
 * Der LevelRestController bietet die meisten Funktionen des LevelServices bequem per REST-API an, sodass das Frontend
 * komfortabel mit der Datenhaltung in der Datenbank interagieren kann.
 */
@RestController
public class LevelRestController {

    public static final String LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE = "Das Level existiert in der DB, jetzt wird der Raum geholt";
    public static final String LEVEL_NICHT_IN_DB_404_LOG_MESSAGE = "Level nicht in DB gefunden, externer Aufrufer erhält 404";
    public static final EntityNichtInDatenbankException LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION = new EntityNichtInDatenbankException("Das Level gibt es nicht in der Datenbank");
    public static final String VERSENDE_EINFACHEN_RAUM_INHALT_ALS_POJO = "Versende einfachen Raum-Inhalt als POJO";
    private final Logger lg = LoggerFactory.getLogger(LevelRestController.class);
    @Autowired
    RaumMobiliarRepository raumMobiliarRepository;
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
    @GetMapping(value = "/api/level/{mobiliarID}")
    public String getGLTFLink(@PathVariable long mobiliarID) {
        lg.info("GET-Anfrage für die gltf-Datei-URL von Mobiliar-ID {}", mobiliarID);
        try {
            return String.format("{\"gltfPfad\":\"%s\"}", levelService.getMobiliar3DModellURI(mobiliarID));
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
     * Liefert mit einem GET-Aufruf den Inhalt eines Levels in Form eines Raum-POJOs, das rudimentär einen Raum abbildet.
     * Wenn Raum-Index oder Level-ID nicht in der Datenbank existieren, wird jeweils passend entweder ein neuer Raum
     * oder ein neues Level angelegt und auf das Raum-POJO gemappt, sodass immer ein valider Output geliefert wird.
     * Dabei ist jedoch wichtig zu beachten, dass Level-IDs von der Datenbank vergeben werden, man kann sich also keine
     * Level-ID "wünschen". Daher ist es ratsam, bei einem neuen Level eine Level-ID anzufragen,
     * die garantiert nicht existiert, beispielsweise '-1'.
     *
     * @param benutzername ist der Benutzername, unter dem ein eventuell nicht vorhandenes Level erstellt werden soll.
     * @param levelID      Die Level-Id, die in der DB angefragt werden soll.
     * @param raumindex    Der Raum-Index aus dem gesuchten Level.
     * @return Ein einfaches, nicht mit der DB verknüpftes Raum-Objekt, das grobe Infos über Raum-Inahalt und Level enthält.
     */
    @GetMapping(value = "/api/level/einfach/{benutzername}/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RaumPOJO getEinfachenRauminhalt(@PathVariable String benutzername,
                                           @PathVariable long levelID,
                                           @PathVariable int raumindex) {
        lg.info("Einfacher Rauminhalt von Level ID {} und Raumindex {} über REST angefragt", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);
        if (angefragtesLevel.isPresent()) {
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);

            // Wenn der RaumIndex zu hoch ist, wirft der LevelService eine NoSuchElementException, deshalb try/catch
            try {

                Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
                lg.info("Raum ist in Datenbank vorhanden, wandle Rauminhalt um");

                // Jetzt holen wir uns das gesamte RaumMobiliar des Raumes...
                List<RaumMobiliar> raumMobiliar = angefragterRaum.getRaumMobiliar();
                long[][] einfacherRaumInhalt = new long[14][22];
                for (RaumMobiliar r : raumMobiliar) {
                    // ... und fügen die Mobiliar-ID an der dazugehörigen X/Y-Stelle im Array ein
                    einfacherRaumInhalt[r.getPositionX()][r.getPositionY()] = r.getMobiliar().getMobiliarId();
                }

                // Da das versenden und Empfangen von @Entities gefährlich ist, geben wir dem Frontend nur ein
                // rudimentäres Raum-POJO mit, das es befüllen soll

                lg.info(VERSENDE_EINFACHEN_RAUM_INHALT_ALS_POJO);
                return new RaumPOJO(
                        angefragtesLevel.get().getLevelId(),
                        angefragtesLevel.get().getErsteller().getBenutzername(),
                        angefragtesLevel.get().getName(),
                        angefragtesLevel.get().getBeschreibung(),
                        einfacherRaumInhalt
                );

            } catch (NoSuchElementException e) {
                // Wenn es den Raum noch nicht gibt, erstellen wir einen neuen und
                // geben einfach einen mit Wänden (ID 0) gefüllten zurück.

                lg.info("Raum ist nicht in der Datenbank wir erstellen einen Neuen, mit Index {}", raumindex);
                Raum raum = new Raum(raumindex, new ArrayList<>());
                angefragtesLevel.get().getRaeume().add(raumindex, raum);
                levelService.bearbeiteLevel(benutzername, angefragtesLevel.get());


                long[][] einfacherRaumInhalt = new long[14][22];
                for (long[] yAchse : einfacherRaumInhalt) {
                    Arrays.fill(yAchse, 0L);
                }

                lg.info(VERSENDE_EINFACHEN_RAUM_INHALT_ALS_POJO);
                // Wieder nur ein Raum-POJO
                return new RaumPOJO(
                        angefragtesLevel.get().getLevelId(),
                        angefragtesLevel.get().getErsteller().getBenutzername(),
                        angefragtesLevel.get().getName(),
                        angefragtesLevel.get().getBeschreibung(),
                        einfacherRaumInhalt
                );
            }
        }
        // Wenn die Level-ID noch nicht in der DB ist, erstellen wir ein neues Level
        lg.info("Level-ID nicht in DB gefunden, erstelle neues Level...");
        Raum raum = new Raum(raumindex, new ArrayList<>());
        List<Raum> raume = new ArrayList<>();
        raume.add(raum);

        Level level = new Level("", "", (byte) 0, raume);
        level = levelService.bearbeiteLevel(benutzername, level);
        lg.info("Level erfolgreich erstellt, es hat die ID {}", level.getLevelId());

        // Ansonsten selbes Spiel wie beim nicht existenten Raum
        long[][] einfacherRaumInhalt = new long[14][22];
        for (long[] yAchse : einfacherRaumInhalt) {
            Arrays.fill(yAchse, 0L);
        }

        lg.info(VERSENDE_EINFACHEN_RAUM_INHALT_ALS_POJO);
        return new RaumPOJO(
                level.getLevelId(),
                level.getErsteller().getBenutzername(),
                level.getName(),
                level.getBeschreibung(),
                einfacherRaumInhalt
        );

    }

    /**
     * Speichert oder überschreibt mit einem PUT-Aufruf den Inhalt eines Levels.
     * Dafür muss im RequestBody ein serialisiertes RaumPOJO existieren.
     * Wenn Raum-Index oder Level-ID nicht in der Datenbank existieren, werden Fehler geworfen, neue Räume oder Level
     * erhält man über GET.
     *
     * @param benutzername wird nicht benötigt, ist jedoch aus Gründen der Einheitlichkeit noch da.
     * @param levelID      Die Level-Id, zu dem der neue Raum gespeichert werden soll.
     * @param raumindex    Der Raum-Index aus dem gesuchten Level.
     * @param raumPOJO     Der einfache Raum aus dem Frontend, mit dem zu verarbeitenden Rauminhalt.
     */
    @PutMapping(value = "/api/level/einfach/{benutzername}/{levelID}/{raumindex}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putEinfachenRauminhalt(@PathVariable String benutzername,
                                       @PathVariable long levelID,
                                       @PathVariable int raumindex,
                                       @RequestBody RaumPOJO raumPOJO) {
        lg.info("Einfachen Rauminhalt zu Level ID {} und Raumindex {} über REST erhalten", levelID, raumindex);
        Optional<Level> angefragtesLevel = levelService.getLevel(levelID);
        if (angefragtesLevel.isPresent()) {
            lg.info(LEVEL_EXISTIERT_IN_DB_JETZT_RAUM_ABFRAGE);

            try {
                // Wenn der RaumIndex zu hoch ist, wirft der LevelService eine NoSuchElementException, die wir fangen sollten.

                Raum angefragterRaum = levelService.getRaum(angefragtesLevel.get(), raumindex);
                lg.info("Raumindex gibt es auch, neuer Rauminhalt wird geschrieben...");

                levelService.speichereRauminhaltueberRaumPOJO(raumPOJO, angefragtesLevel.get(), angefragterRaum);

                lg.info("Neuen Raum erfolgreich gespeichert");

            } catch (NoSuchElementException e) {
                lg.warn("levelService.getRaum lieferte NoSuchElementException, breche mit Error 400 ab. " +
                        "Vielleicht wurde zu Beginn das GET vergessen?");
                throw new LevelAttributZugriffsException("Das Level gab es in der Datenbank, aber den Raum nicht.");
            }
        } else {
            lg.warn(LEVEL_NICHT_IN_DB_404_LOG_MESSAGE);
            throw LEVEL_ENTITY_NICHT_IN_DATENBANK_EXCEPTION;
        }

    }

}
