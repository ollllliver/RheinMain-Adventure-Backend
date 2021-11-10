package de.hsrm.mi.swt.rheinmainadventure.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {
  Benutzer findByBenutzername(String benutzername);

}