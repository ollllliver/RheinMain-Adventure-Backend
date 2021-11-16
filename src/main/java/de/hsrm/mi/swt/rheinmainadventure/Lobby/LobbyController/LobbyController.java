package de.hsrm.mi.swt.rheinmainadventure.Lobby.LobbyController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lobby")
class LobbyController {

    Logger lg = LoggerFactory.getLogger(LobbyController.class);

    /**
     * Erzeugt Logging-Ausgabe, wenn auf '/' zugegriffen wird. Enthält absichtlich
     * einen Benennungskonventionsfehler, um SonarQube zu testen.
     *
     * @return Hallo Welt.html, da nur Logging erzeugt werden soll
     */
    @GetMapping("/")
    public String lebenszeichen() {
        lg.info("Aufruf wurde erkannt. Hurra!");
        return "Hallo Welt";
    }

    @GetMapping("/create")
    public String login() {
        lg.info("Aufruf wurde erkannt. Hurra!");
        return "Hallo Login";
    }

}
