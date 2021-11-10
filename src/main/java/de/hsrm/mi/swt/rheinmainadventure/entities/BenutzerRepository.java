package de.hsrm.mi.swt.rheinmainadventure.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenutzerRepository extends CrudRepository<Benutzer, Long> {
  Benutzer findByBenutzername(String benutzername);

}