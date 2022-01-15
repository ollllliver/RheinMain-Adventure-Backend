package de.hsrm.mi.swt.rheinmainadventure.spiel.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LevelAttributZugriffsException extends RuntimeException {
    public LevelAttributZugriffsException(String message) {
        super(message);
    }
}
