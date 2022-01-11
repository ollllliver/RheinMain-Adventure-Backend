package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LevelNotFoundException extends RuntimeException {
}
