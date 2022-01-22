package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    Level findByName(String name);
}
