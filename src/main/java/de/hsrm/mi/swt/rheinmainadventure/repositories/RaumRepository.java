package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaumRepository extends JpaRepository<Raum, Long> {

    List<Raum> findAllByLevel_LevelIdOrderByRaumIndex(Long levelId);

    Raum findAllByLevel_LevelIdAndRaumIndex(long levelId, int raumIndex);
}
