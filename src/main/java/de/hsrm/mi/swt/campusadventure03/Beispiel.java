package de.hsrm.mi.swt.campusadventure03;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
class Beispiel {

  Logger lg = LoggerFactory.getLogger(Beispiel.class);

  @GetMapping("/")
  public String Lebenszeichen() {
    lg.info("Aufruf wurde erkannt. Hurra!");
    return "";
  }

}

