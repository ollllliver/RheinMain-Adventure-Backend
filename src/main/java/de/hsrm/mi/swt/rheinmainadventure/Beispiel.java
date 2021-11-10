package de.hsrm.mi.swt.rheinmainadventure;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
class Beispiel {

  Logger lg = LoggerFactory.getLogger(Beispiel.class);

  /**
   * Erzeugt Logging-Ausgabe, wenn auf '/' zugegriffen wird.
   * Enthält absichtlich einen Benennungskonventionsfehler, um SonarQube zu testen.
   *
   * @return Hallo Welt.html, da nur Logging erzeugt werden soll
   */
  @GetMapping("/")
  public String lebenszeichen() {
    lg.info("Aufruf wurde erkannt. Hurra!");
    return "Hallo Welt";
  }

}

