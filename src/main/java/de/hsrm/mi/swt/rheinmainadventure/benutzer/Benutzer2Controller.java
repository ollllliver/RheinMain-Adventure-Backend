package de.hsrm.mi.swt.rheinmainadventure.benutzer;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/benutzer")
public class Benutzer2Controller {



    private static final List<Benutzer2> BENUTZER = Arrays.asList(
            new Benutzer2(1,"Peter Enis"),
            new Benutzer2(2, "Klaus Maus"),
            new Benutzer2(3, "Angelika Nuss")
    );

    public Benutzer2 getStudent(@PathVariable("benutzerId") Integer benutzerId){
        return BENUTZER.stream()
                .filter(benutzer2 -> benutzerId.equals(benutzerId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Benutzer " + benutzerId + " existiert nicht."));
    }
}
