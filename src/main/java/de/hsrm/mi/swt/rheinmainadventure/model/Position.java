package de.hsrm.mi.swt.rheinmainadventure.model;

import java.util.Map;

import javax.persistence.Embeddable;

@Embeddable
public class Position {
    
    private Map<String, Integer> position;

    public Position() {
        this.position.put("x", null);
        this.position.put("y", null);
    }

    public Position(Map<String, Integer> position) {
        this.position = position;
    }

    public Map<String, Integer> getPosition() {
        return position;
    }

    public void setPosition(Map<String, Integer> position) {
        this.position = position;
    }

}
