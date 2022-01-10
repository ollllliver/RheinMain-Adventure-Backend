package de.hsrm.mi.swt.rheinmainadventure.spiel;

import de.hsrm.mi.swt.rheinmainadventure.entities.RaumMobiliar;
import de.hsrm.mi.swt.rheinmainadventure.repositories.MobiliarRepository;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/api/level/{levelID}/{raumindex}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RaumMobiliar> getRauminhalt(@PathVariable long levelID, @PathVariable int raumindex) {
        return levelService.getRaum(levelService.getLevel(levelID).get(), raumindex).getRaumMobiliar();
    }

    @GetMapping(value = "/api/level/{mobiliarID}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource getGLTFObject(@PathVariable long mobiliarID) {
        return new FileSystemResource("src/main/resources/" + mobiliarRepository.getById(mobiliarID).getModellURI());
    }   

    @PostMapping(value = "/api/levelEditor/speichern", consumes = MediaType.APPLICATION_JSON_VALUE)
    // jackson deserialiszer
    // axion senden von daten
    public String speichereLevel(@RequestBody Map<String, ?> levelData) throws NoSuchFieldException {
        Logger logger = LoggerFactory.getLogger(DemoLevelRestController.class);

        List<List<Object>> karte = (ArrayList<List<Object>>) levelData.get("karte");

        String levelName = levelData.get("name").toString();
        int minSpieler = Integer.parseInt(levelData.get("minSpieler").toString());
        int maxSpieler = Integer.parseInt(levelData.get("maxSpieler").toString());
        levelService.levelHinzufuegen(levelName, minSpieler, maxSpieler, (byte)17, karte);

        //logger.info(levelData[13][5].toString());











        return levelData.toString();
    }
    

}
