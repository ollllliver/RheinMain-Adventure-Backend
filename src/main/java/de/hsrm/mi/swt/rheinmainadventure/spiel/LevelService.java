package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.mobiliar.Mobiliar;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;

import java.util.List;

public interface LevelService {
    List<Level> alleLevel();

    Level getLevel(long levelId);

    List<Raum> getAlleRaumeImLevel(Level level);

    Raum getRaum(long levelId, long raumindex);

    Mobiliar[][] getMobiliarImRaum(Raum raum);

    Position getStartPositionImRaum(Raum raum);
}
