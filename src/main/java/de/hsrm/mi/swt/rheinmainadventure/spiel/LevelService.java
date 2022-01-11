package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Mobiliar;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LevelService {
    List<Level> alleLevel();

    Optional<Level> getLevel(long levelId);

    Level bearbeiteLevel(String benutzername, Level externesLevel);

    void loescheLevel(long levelId);

    List<Raum> getAlleRaumeImLevel(Level level);

    Raum getRaum(Level level, int raumindex);

    String getMobiliar3DModellURI(long mobiliarID);

    Map<Position, Mobiliar> getMobiliarImRaum(Raum raum);

    Position getStartPositionImRaum(Raum raum);
}
