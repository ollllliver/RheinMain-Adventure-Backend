package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Mobiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobiliarRepository extends JpaRepository<Mobiliar, Long> {
    Mobiliar getMobiliarByMobiliarId(long mobiliarID);
}
