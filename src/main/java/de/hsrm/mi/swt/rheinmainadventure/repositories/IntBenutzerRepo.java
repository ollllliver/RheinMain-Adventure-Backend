package de.hsrm.mi.swt.rheinmainadventure.repositories;

import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;


//@EnableJpaRepositories(basePackageClasses = IntBenutzerRepo.class)
public interface IntBenutzerRepo extends JpaRepository<Benutzer, Long> { /*JPA extends CRUD repository in batch deleting etc*/
    Benutzer findByBenutzername(String benutzername);
}