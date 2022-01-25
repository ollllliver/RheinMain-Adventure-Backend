package de.hsrm.mi.swt.rheinmainadventure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FalscheAnmeldeDatenException extends RuntimeException {
    public FalscheAnmeldeDatenException(String message) {
        super(message);
    }
}
