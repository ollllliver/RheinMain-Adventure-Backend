package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Eine Demo-Rest Api, um das Frontend probeweise anzukabeln.
 * Sehr schlecht gecodet, einfach nur um das Frontend benutzbar zu machen
 */
@CrossOrigin
@RestController
public class DemoLevelRestController {

    @Autowired
    LevelService levelService;

    @Autowired
    MobiliarRepository mobiliarRepository;

    @GetMapping(value = "/level/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RaumMobiliar> getRauminhalt(@PathVariable long levelID, @PathVariable int raumindex) {
        return levelService.getRaum(levelService.getLevel(levelID).get(), raumindex).getRaumMobiliar();
    }

    @GetMapping(value = "/level/{mobiliarID}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource getGLTFObject(@PathVariable long mobiliarID) {
        return new FileSystemResource("src/main/resources/" + mobiliarRepository.getById(mobiliarID).getModellURI());
    }

}
