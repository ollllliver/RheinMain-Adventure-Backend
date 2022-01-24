package de.hsrm.mi.swt.rheinmainadventure.controller;
 
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BenutzerNichtGefundenException extends RuntimeException {
   public BenutzerNichtGefundenException(String message) {
       super(message);
   }
}
