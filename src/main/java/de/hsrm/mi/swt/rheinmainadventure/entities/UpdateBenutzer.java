package de.hsrm.mi.swt.rheinmainadventure.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBenutzer {

    @Column(unique = true, nullable = false)
    @NotNull(message = "Name kann nicht leer sein")
    private String benutzername;

    @NotNull(message = "Passwort darf nicht leer sein")
    private String passwort;
    
}
