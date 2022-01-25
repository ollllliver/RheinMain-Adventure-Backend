package de.hsrm.mi.swt.rheinmainadventure.spiel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.rheinmainadventure.model.SchluesselUpdate;

public class SchluesselUpdateTest {

    private SchluesselUpdate u;

    @Test
    public void updaterTest(){
        u = new SchluesselUpdate("Tuer", 2, "{4;4}");
        assertTrue(u.getObjectName().equals("Tuer"));
        assertTrue(u.getAnzSchluessel() == 2);
        assertTrue(u.getKoordinatenArray().equals("{4;4}"));
    }
    
}
