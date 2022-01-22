package de.hsrm.mi.swt.rheinmainadventure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class Beispieltest {

    @Test
    @DisplayName("Test läuft durch.")
    public void erfolgreich() {
        assertTrue(true);
    }

}