package de.hsrm.mi.swt.rheinmainadventure.spiel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.rheinmainadventure.model.SchluesselUpdate;

public class SchluesselUpdateTest {

    private SchluesselUpdate u;

    @Test
    public void updaterTest(){
        u = new SchluesselUpdate("Tuer", 2, "{4;4}");
        assertEquals("Tuer", u.getObjectName());
        assertEquals(2, u.getAnzSchluessel());
        assertEquals("{4;4]}", u.getKoordinatenArray());
    }
    
}
