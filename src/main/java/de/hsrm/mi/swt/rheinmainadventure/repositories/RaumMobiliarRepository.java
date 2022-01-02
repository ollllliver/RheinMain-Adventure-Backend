package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaumMobiliarRepository extends JpaRepository<RaumMobiliar, Long> {


    /**
     * Findet sämtliches Mobiliar, das sich in einem Raum befindet.
     *
     * @param raumId is die Raum-ID, zu der das Mobiliar nachgeschlagen werden soll
     * @return Eine Liste von RaumMobiliar-Objekten, aus der man über die Mobiliar-ID dann das Mobiliar enthält
     * sowie die X/Y-Position im Level
     */
    List<RaumMobiliar> findAllByRaum_RaumId(Long raumId);
}
