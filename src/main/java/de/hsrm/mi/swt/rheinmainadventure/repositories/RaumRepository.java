package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaumRepository extends JpaRepository<Raum, Long> {

    /**
     * Sucht zu einer Level-ID alle Räume dieses Levels heraus
     *
     * @param levelId Die Level-ID des Levels, zu dem man alle Räume finden möchte.
     * @return eine nicht sortierte Liste von allen Räumen eines Levels, wenn die Level-ID existiert
     */
    List<Raum> findAllByLevel_LevelId(Long levelId);
}
