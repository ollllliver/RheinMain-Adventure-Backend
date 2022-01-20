package de.hsrm.mi.swt.rheinmainadventure.controller;

import de.hsrm.mi.swt.rheinmainadventure.lobby.LobbyService;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.Position;
import de.hsrm.mi.swt.rheinmainadventure.model.SchluesselUpdate;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.spiel.SpielService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SpielerController {

    private final Logger logger = LoggerFactory.getLogger(SpielerController.class);

    @Autowired
    private SpielService spielService;
    @Autowired
    private LobbyService lobbyService;

    /**
     * Stomp Mapping für das empfangen von neuen Positionen der Spieler und senden
     * der Spieler mit neuer Position an alle Mitspieler
     *
     * @param pos     ist eine Position, die ein Spieler einnehmen möchte
     * @param lobbyID ist die ID der Lobby, in der der Spieler gerade spielt.
     * @param name    ist der Name des Spielers, der seine Position ändern möchte.
     * @return den Spieler mit der aktualisierten Position
     * @throws Exception
     */
    @MessageMapping("/topic/spiel/{lobbyID}/pos/{name}")
    @SendTo("/topic/spiel/{lobbyID}")
    public Spieler updatePosition(@Payload Position pos, @DestinationVariable String lobbyID,
            @DestinationVariable String name) throws Exception {
        //logger.info("SpielerController.updatePosition: Payload=" + pos + ", lobbyID=" + lobbyID + ", name: " + name);
        // broker.convertAndSend("/topic/spiel/" + lobbyID, pos); //nur Test
        Spieler spieler = spielService.getSpieler(lobbyID, name);
        return spielService.positionsAktualisierung(spieler, pos);
    }

    @MessageMapping("/topic/spiel/{lobbyID}/key")
    @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public SchluesselUpdate schluesselEingesammelt(@Payload int objectID, @DestinationVariable String lobbyID) throws Exception {
        logger.info("ES wurde interagiert mit: " + objectID);
        spielService.anzahlSchluesselErhoehen(spielService.findeSpiel(lobbyID));
        logger.info("Anzahl Schluessel in Spiel" + lobbyID + " betraegt"
            + spielService.findeSpiel(lobbyID).getAnzSchluessel());
        SchluesselUpdate update = new SchluesselUpdate(spielService.findeSpiel(lobbyID).getAnzSchluessel(), objectID);
        return update;
    }

    @MessageMapping("/topic/spiel/{lobbyID}/tuer")
    @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public SchluesselUpdate tuerOEffnen(@Payload String objectID, @DestinationVariable String lobbyID) throws Exception {
        //TODO payload richtig abfangen
        SchluesselUpdate update = new SchluesselUpdate(spielService.findeSpiel(lobbyID).getAnzSchluessel(), 0);
        if(spielService.findeSpiel(lobbyID).getAnzSchluessel()==0){
            logger.info("Du brauchst erst einen Schlüssel");
            return update;
        }else{
            logger.info(objectID + " wird aufgeschlossen");
            spielService.anzahlSchluesselVerringern(spielService.findeSpiel(lobbyID));
            logger.info("Anzahl Schluessel in Spiel" + lobbyID + " betraegt"
                + spielService.findeSpiel(lobbyID).getAnzSchluessel());

            return update;
        }

    }

    /**
     * @param msg     ist die Nachricht, welche vom Frontend als reference vermittelt wird
     * @param lobbyID ist die ID der Lobby, in welcher die Spieler nach beenden des Spiels umschalten
     * @throws Exception
     */
    @MessageMapping("/topic/lobby/{lobbyID}")
    public void beendeSpiel(@Payload LobbyMessage msg, @DestinationVariable String lobbyID) throws Exception {
        logger.info("SpielerController.BackToLobby: lobbyID=" + lobbyID);
        if (msg.getTyp().equals(NachrichtenCode.BEENDE_SPIEL)) {
            lobbyService.zurueckZurLobby(lobbyID);
        }
    }

    @MessageMapping("/topic/spiel")
    // @SendTo("/topic/spiel/")
    public void updatePosition(@Payload String s) throws Exception {
        // logger.info("\n\n\n\n\n\nUpdate Position: " + s +"\n\n\n\n\n\n");
    }
}
