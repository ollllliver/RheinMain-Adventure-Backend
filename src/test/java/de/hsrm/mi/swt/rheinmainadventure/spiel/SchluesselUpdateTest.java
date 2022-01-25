package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.model.SchluesselUpdate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchluesselUpdateTest {

    private SchluesselUpdate u;

    @Test
    void updaterTest() {
        u = new SchluesselUpdate("Tuer", 2, "{4;4}");
        assertEquals("Tuer", u.getObjectName());
        assertEquals(2, u.getAnzSchluessel());
        assertEquals("{4;4}", u.getKoordinatenArray());
    }
    
}
