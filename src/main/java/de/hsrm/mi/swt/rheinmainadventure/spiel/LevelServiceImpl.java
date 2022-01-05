package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.*;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.repositories.LevelRepository;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.repositories.RaumMobiliarRepository;
import de.hsrm.mi.swt.rheinmainadventure.repositories.RaumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final RaumRepository raumRepository;
    private final MobiliarRepository mobiliarRepository;
    private final RaumMobiliarRepository raumMobiliarRepository;

    private final Logger lg = LoggerFactory.getLogger(LevelServiceImpl.class);

    public LevelServiceImpl(LevelRepository levelRepository,
                            RaumRepository raumRepository,
                            MobiliarRepository mobiliarRepository,
                            RaumMobiliarRepository raumMobiliarRepository) {
        this.levelRepository = levelRepository;
        this.raumRepository = raumRepository;
        this.mobiliarRepository = mobiliarRepository;
        this.raumMobiliarRepository = raumMobiliarRepository;
    }

    @Override
    public List<Level> alleLevel() {
        return levelRepository.findAll();
    }

    @Override
    public Optional<Level> getLevel(long levelId) {
        return levelRepository.findById(levelId);
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

        Optional<Level> dbLevel = getLevel(externesLevel.getLevelId());
        if (dbLevel.isPresent()) {
            lg.info("Level existiert in DB, Räume werden abgefragt");
            return raumRepository.findAllByLevel_LevelIdOrderByRaumIndex(dbLevel.get().getLevelId());
        } else {
            lg.warn("Das hereingereichte Level wurde nicht in der Datenbank gefunden!");
            throw new NoSuchElementException();
        }
    }

    /**
     * @param externesLevel Das Level, zu dem man den bestimmten Raum finden möchte.
     * @param raumIndex     An welcher Stelle der gesuchte Raum im Level vorkommt.
     * @return Den gesuchten Raum, wenn die Kombination Level - Raum-Index existiert
     * @throws NoSuchElementException Wenn es das Level nicht in der DB ist oder der Raum-Index Out of Bounds ist
     */
    @Override
    public Raum getRaum(Level externesLevel, int raumIndex) throws NoSuchElementException {
        lg.info("Im Level {} wird nach dem Raum Nummer {} gesucht", externesLevel, raumIndex);
        Optional<Level> dbLevel = getLevel(externesLevel.getLevelId());
        if (dbLevel.isPresent() && dbLevel.get().getRaeume().size() >= raumIndex) {
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

    /**
     * Findet sämtliches Mobiliar, das sich in einem Raum befindet.
     *
     * @param externerRaum ist der Raum, zu der das Mobiliar nachgeschlagen werden soll
     * @return Eine Map von Mobiliar-Objekten, mit der Position als Schlüssel sowie die X/Y-Position im Level
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
            if (entry.getValue().getMobiliartyp().equals(Mobiliartyp.EINGANG)) {
                // Der Schlüssel der Map ist die Mobiliar-Position, also geben wir die zurück
                lg.info("Gefunden!");
                return entry.getKey();
            }
        }
        lg.warn("Der gesuchte Raum hat keine Startposition!");
        throw new NoSuchElementException();

    }
}
