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
     */
    @MessageMapping("/topic/spiel/{lobbyID}/pos/{name}")
    @SendTo("/topic/spiel/{lobbyID}")
    public Spieler updatePosition(@Payload Position pos, @DestinationVariable String lobbyID,
                                  @DestinationVariable String name) {
        Spieler spieler = spielService.getSpieler(lobbyID, name);
        return spielService.positionsAktualisierung(spieler, pos);
    }

    @MessageMapping("/topic/spiel/{lobbyID}/key")
    @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public SchluesselUpdate schluesselEingesammelt(@Payload String position, @DestinationVariable String lobbyID) {
        logger.info("ES wurde interagiert mit Index: {}", position);
        spielService.anzahlSchluesselErhoehen(spielService.findeSpiel(lobbyID));
        logger.info("Anzahl Schluessel in Spiel {} betraegt {}", lobbyID, spielService.findeSpiel(lobbyID).getAnzSchluessel());
        return new SchluesselUpdate(spielService.findeSpiel(lobbyID).getAnzSchluessel(), position);
    }

    @MessageMapping("/topic/spiel/{lobbyID}/tuer")
    @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public SchluesselUpdate tuerOEffnen(@Payload String position, @DestinationVariable String lobbyID) {
        //TODO payload richtig abfangen
        SchluesselUpdate update = new SchluesselUpdate(spielService.findeSpiel(lobbyID).getAnzSchluessel(), position);
        if (spielService.findeSpiel(lobbyID).getAnzSchluessel() == 0) {
            logger.info("Du brauchst erst einen Schlüssel");
            return update;
        } else {
            logger.info("{} wird aufgeschlossen", position);
            spielService.anzahlSchluesselVerringern(spielService.findeSpiel(lobbyID));
            logger.info("Anzahl Schluessel in Spiel {} betraegt {}", lobbyID,
                    spielService.findeSpiel(lobbyID).getAnzSchluessel());

            return update;
        }

    }

    /**
     * @param msg     ist die Nachricht, welche vom Frontend als reference vermittelt wird
     * @param lobbyID ist die ID der Lobby, in welcher die Spieler nach beenden des Spiels umschalten
     */
    @MessageMapping("/topic/lobby/{lobbyID}")
    public void beendeSpiel(@Payload LobbyMessage msg, @DestinationVariable String lobbyID) {
        logger.info("SpielerController.BackToLobby: lobbyID= {}", lobbyID);
        if (msg.getTyp().equals(NachrichtenCode.BEENDE_SPIEL)) {
            lobbyService.zurueckZurLobby(lobbyID);
        }
    }
}
