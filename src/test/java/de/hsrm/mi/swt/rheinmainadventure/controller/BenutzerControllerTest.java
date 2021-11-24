package de.hsrm.mi.swt.rheinmainadventure.controller;

import org.junit.jupiter.api.Test;

class BenutzerControllerTest {

    @Test
    void getAll() {
        // TODO: Bei Vorhandenen Nutzern kommen die auch wieder zurück, wenn es keine Nutzer gibt, ist die Liste leer
    }

    @Test
    void getByBenutzername() {
        //TODO: Liefert false, bei Nichtexistenz, True wenn es den Nutzer gibt
    }

    @Test
    void registrieren() {
        // TODO Neuen Nutzer registrieren leifert CREATED, nochmal den selben anlegen dann CONFLICT
    }

    @Test
    void anmelden() {
        //TODO: Benutzername und Passwort m¨ssen zum anmelden übereinstimmen
    }
}