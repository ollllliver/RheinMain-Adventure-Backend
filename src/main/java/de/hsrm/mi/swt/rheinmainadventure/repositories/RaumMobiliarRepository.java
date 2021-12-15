package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Raum;
import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaumMobiliarRepository extends JpaRepository<RaumMobiliar, Long> {

    List<RaumMobiliar> findAllByRaum_RaumId(int raumId);
}
