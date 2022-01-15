package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.benutzer.BenutzerService;
import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    private final Logger lg = LoggerFactory.getLogger(LevelServiceImpl.class);
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private RaumRepository raumRepository;
    @Autowired
    private MobiliarRepository mobiliarRepository;
    @Autowired
    private RaumMobiliarRepository raumMobiliarRepository;
    @Autowired
    private BenutzerService benutzerService;
    @Autowired
    IntBenutzerRepo benutzerRepository;

    @Override
    public List<Level> alleLevel() {
        return levelRepository.findAll();
    }

    @Override
    public Optional<Level> getLevel(long levelId) {
        return levelRepository.findById(levelId);
    }

    /**
     * Ersetzt ein Level aus der Datenbank mit dem Level aus dem Übergabeparameter,
     * oder legt es neu an, wenn es noch nicht existiert,
     *
     * @param benutzername  Ist der Benutzername des Nutzers, der das Level erstellt hat.
     * @param externesLevel Ist das Level, das in die Datenbank übernommen werden soll.
     * @return Das neue Level, synchron mit der Datenbank
     */
    @Override
    @Transactional
    public Level bearbeiteLevel(String benutzername, Level externesLevel) {
        try {
            lg.info("LevelService - bearbeiteLevel");

            // Wenn externes Level keine Level-ID hat, müssen wir das abfangen
            Optional<Long> optionalLevelId = Optional.ofNullable(externesLevel.getLevelId());
            Optional<Level> levelZumAktualisieren = Optional.empty();

            // Den Benutzer anhand des Benutzernamens herausfinden.
            Benutzer ersteller = benutzerService.findeBenutzer(benutzername);
            lg.info("DB erfolgreich nach Benutzer abgefragt");

            if (optionalLevelId.isPresent()) {
                lg.info("Das externe Level hat schon eine ID.");
                levelZumAktualisieren = levelRepository.findById(externesLevel.getLevelId());

                lg.info("DB erfolgreich nach externer Level-ID abgefragt");
            }

            // Wenn das Optional befüllt ist, haben wir ein Level und können es einfach updaten
            // Speichern ist (hoffentlich) nicht nötig, da der Spaß hier @Transactional ist
            if (levelZumAktualisieren.isPresent()) {
                lg.info("Level aus DB erhält ein Update");
                Level level = levelZumAktualisieren.get();

                level.setName(externesLevel.getName());
                level.setBeschreibung(externesLevel.getBeschreibung());
                level.setBewertung(externesLevel.getBewertung());
                level.setRaeume(externesLevel.getRaeume());
                level.setErsteller(externesLevel.getErsteller());

                lg.info("Level aktualisiert");

                return level;

            } else {
                // Wenn das Optional leer ist, gibt es das Level noch nicht in der DB und wir legen es neu an

                lg.info("Neues Level wird in DB angelegt");

                // Alles abspeichern und beim Anbieter hinterlegen
                for (Raum raum : externesLevel.getRaeume()) {
                    raum.setLevel(externesLevel);
                    raumMobiliarRepository.saveAll(raum.getRaumMobiliar());
                }
                raumRepository.saveAll(externesLevel.getRaeume());
                externesLevel.setErsteller(ersteller);

                Level level = levelRepository.save(externesLevel);
                lg.info("Level eingefügt");

                return level;
            }
        } catch (OptimisticLockException ole) {
            lg.error("OptimisticLockException ist aufgetreten! Eventuell einfach nochmal probieren?");
            throw ole;
        }
    }


    /**
     * Löscht ein Level über eine gegebene Level-ID.
     *
     * @param levelId ist die Level-ID des Levels, das aus der Datenbank gelöscht werden soll.
     */
    @Override
    public void loescheLevel(long levelId) {
        Optional<Level> zuLoeschen = levelRepository.findById(levelId);
        if (zuLoeschen.isPresent()) {
            for (Raum raum : zuLoeschen.get().getRaeume()) {
                raumMobiliarRepository.deleteAllInBatch(raum.getRaumMobiliar());
                raumRepository.deleteById(raum.getRaumId());
            }

            levelRepository.deleteById(levelId);
            lg.info("Level aus DB gelöscht, Anzeige ist raus.");
        }
    }

    /**
     * Sucht zu einem Level alle Räume dieses Levels heraus.
     *
     * @param externesLevel Das Level, zu dem man alle Räume finden möchte.
     * @return Eine nach Raum-Index sortierte Liste von allen Räumen eines Levels, wenn die Level-ID existiert.
     * @throws NoSuchElementException Wenn das hereingereichte Level über seine ID nicht in der DB gefunden wurde.
     */
    @Override
    public List<Raum> getAlleRaumeImLevel(Level externesLevel) throws NoSuchElementException {
        lg.info("Level {} wird in der Datenbank nach seinen Räumen abgefragt", externesLevel);

        Optional<Level> dbLevel = levelRepository.findById(externesLevel.getLevelId());
        if (dbLevel.isPresent()) {
            lg.info("Level existiert in DB, Räume werden abgefragt");
            return raumRepository.findAllByLevel_LevelIdOrderByRaumIndex(dbLevel.get().getLevelId());
        } else {
            lg.warn("Das hereingereichte Level wurde nicht in der Datenbank gefunden!");
            throw new NoSuchElementException();
        }
    }

    /**
     * Liefert von einem Level-Objekt den Raum an der gesuchten Stelle in der Raum-Abfolge.
     *
     * @param externesLevel Das Level, zu dem man den bestimmten Raum finden möchte.
     * @param raumIndex     An welcher Stelle der gesuchte Raum im Level vorkommt.
     * @return Den gesuchten Raum, wenn die Kombination Level - Raum-Index existiert
     * @throws NoSuchElementException Wenn es das Level nicht in der DB ist oder der Raum-Index Out of Bounds ist
     */
    @Override
    public Raum getRaum(Level externesLevel, int raumIndex) throws NoSuchElementException {
        lg.info("Im Level {} wird nach dem Raum Nummer {} gesucht", externesLevel, raumIndex);
        Optional<Level> dbLevel = levelRepository.findById(externesLevel.getLevelId());
        if (dbLevel.isPresent() && dbLevel.get().getRaeume().size() - 1 >= raumIndex) {
            lg.info("Passendes Level existiert, jetzt nur noch in der DB den passenden Raum finden.");
            return raumRepository.findAllByLevel_LevelIdAndRaumIndex(dbLevel.get().getLevelId(), raumIndex);
        } else {
            if (dbLevel.isEmpty()) {
                lg.warn("Das hereingereichte Level wurde nicht in der Datenbank gefunden!");
            } else {
                lg.warn("Das gesuchte Level existiert, der Raum-Index ist jedoch Out of Bounds!");
            }
            throw new NoSuchElementException();
        }
    }

    @Override
    public String getMobiliar3DModellURI(long mobiliarID) {
        return mobiliarRepository.getById(mobiliarID).getModellURI();
    }


    /**
     * Findet sämtliches Mobiliar, das sich in einem Raum befindet.
     *
     * @param externerRaum ist der Raum, zu dem das Mobiliar nachgeschlagen werden soll
     * @return Eine Map von Mobiliar-Objekten, mit der Position als Schlüssel
     * @throws NoSuchElementException Wenn es den Raum nicht in der Datenbank gibt
     */
    @Override
    public Map<Position, Mobiliar> getMobiliarImRaum(Raum externerRaum) throws NoSuchElementException {
        lg.info("Für den Raum {} wird sämtliches Mobiliar abgefragt", externerRaum);
        Optional<Raum> dbRaum = raumRepository.findById(externerRaum.getRaumId());
        if (dbRaum.isPresent()) {
            lg.info("Der Raum existiert schonmal in der DB...");
            List<RaumMobiliar> raumMobiliar = raumMobiliarRepository.findAllByRaum_RaumId(dbRaum.get().getRaumId());
            lg.info("und hat auch hinterlegtes Mobiliar. Jetzt muss das nur noch in der DB existieren.");

            // Eigentlich nur eine For-each Schleife, die die RaumMobiliar-Objekte in
            // Position und Mobiliar trennt und in eine Map steckt aber der Flex muss sein
            return raumMobiliar.stream().collect(
                    Collectors.toMap(
                            rmTuple ->
                                    new Position(
                                            rmTuple.getPositionX(),
                                            rmTuple.getPositionY()
                                    ),
                            RaumMobiliar::getMobiliar
                    )
            );
        } else {
            lg.warn("Der gesuchte Raum ist der Datenbank unbekannt!");
            throw new NoSuchElementException();
        }
    }


    /**
     * Findet die in einem Raum vom Kartenersteller gesetzte Startposition.
     *
     * @param raum Der Raum, der nach der Position der Startposition abgesucht werden soll
     * @return Ein Positions-Objekt, dass die X/Y-Position der Startposition enthält
     * @throws NoSuchElementException Wenn es keine Startposition im Raum gibt
     */
    @Override
    public Position getStartPositionImRaum(Raum raum) throws NoSuchElementException {
        lg.info("Für den Raum {} soll die Startposition gefunden werden", raum);
        Map<Position, Mobiliar> mobiliarMap = getMobiliarImRaum(raum);
        lg.info("Mobiliar existiert, alles erfolgreich abgefragt.");

        lg.info("Startposition wird gesucht...");
        // Gehe sämtliches Mobiliar im Raum ab
        for (Map.Entry<Position, Mobiliar> entry : mobiliarMap.entrySet()) {
            // Wenn das aktuelle Mobiliar vom Typ Start ist, haben wir unseren Treffer
            if (Mobiliartyp.EINGANG.equals(entry.getValue().getMobiliartyp())) {
                // Der Schlüssel der Map ist die Mobiliar-Position, also geben wir die zurück
                lg.info("Gefunden!");
                return entry.getKey();
            }
        }
        lg.warn("Der gesuchte Raum hat keine Startposition!");
        throw new NoSuchElementException();

    }
}
