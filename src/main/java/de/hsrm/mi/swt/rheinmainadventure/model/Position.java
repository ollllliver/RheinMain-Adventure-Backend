package de.hsrm.mi.swt.rheinmainadventure.model;

import java.util.HashMap;
import java.util.Map;

public class Position {

    private Map<Character, Integer> positionData;

    public Position() {
        this.positionData = new HashMap<>();
        this.positionData.put('x', null);
        this.positionData.put('y', null);
        this.positionData.put('z', null);
    }

    public Position(int x, int y) {
        this.positionData = new HashMap<>();
        this.positionData.put('x', x);
        this.positionData.put('y', y);
        this.positionData.put('z', y);
    }

    public Position(Map<Character, Integer> positionData) {
        this.positionData = positionData;
    }

    public Map<Character, Integer> getPositionData() {
        return positionData;
    }

    public void setPositionData(Map<Character, Integer> positionData) {
        this.positionData = positionData;
    }
}
