package de.hsrm.mi.swt.rheinmainadventure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Beispieltest {

    @Test
    @DisplayName("Test läuft durch.")
    public void erfolgreich() {
        assertTrue(true);
    }

}