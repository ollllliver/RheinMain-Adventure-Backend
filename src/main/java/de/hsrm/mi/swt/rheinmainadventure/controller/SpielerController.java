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

    /**
     * Stomp Mapping für das interagieren mit Objecten (Tuer und Schluessel), senden
     * ein Update Packet an alle im Frontend die auf die LobbyID subscribt haben
     *
     * @param stompPacker beinhaltet Stompnachricht mit Spielerposition x/y und Spielername
     * @param lobbyID 
     * @param objectName beinhaltet Objekttyp
     * @return SchluesselUpdater mit den Benötigten Daten zum verarbeiten im
     * Frontend (Interaktion, anzSchluess, Koordinaten des Objects)
     * @throws Exception
     */
    @MessageMapping("/topic/spiel/{lobbyID}/{objectName}")
    @SendTo("/topic/spiel/{lobbyID}/schluessel")
    public SchluesselUpdate schluesselEingesammelt(@Payload String stompPacket, @DestinationVariable String lobbyID,
                                                   @DestinationVariable String objectName) {
        // TODO enum erstellen für interagierNamen

        String[] splitStompPacket = stompPacket.split(";");
        String posX = splitStompPacket[0];
        String posZ = splitStompPacket[1];
        String spielerName = splitStompPacket[2];
        String position = posX + ";" + posZ;
        logger.info("Spieler {} möchte mit Schlüssel auf Position {} interagieren", spielerName, position);

        if (objectName.equals("Schlüssel")) {
            // Wenn mit Schlüssel interagiert wird, wird der Counter hochgesetzt und das
            spielService.anzahlSchluesselErhoehen(spielService.findeSpiel(lobbyID));
            logger.info("Anzahl Schluessel in Spiel {} betraegt {} Spieler {} erhält 10 Punkte",
                    lobbyID, spielService.findeSpiel(lobbyID).getAnzSchluessel(), spielerName);
            // Update Packet verschickt
            SchluesselUpdate update = new SchluesselUpdate(objectName,
                    spielService.findeSpiel(lobbyID).getAnzSchluessel(), position);
            // Score von dem Spieler dessen name mitgegeben wurde erhoehen
            spielService.scoreErhoehen(spielService.getSpieler(lobbyID, spielerName), 10);
            logger.info("SpielerScore: {}", spielService.getSpieler(lobbyID, spielerName).getScore());
            return update;
        }
        if (objectName.equals("Tür")) {
            // Wenn SchluesselAnzahl größer 0, wird der Counter verringert und darf die Tuer
            // geöffnet werden...
            if (spielService.findeSpiel(lobbyID).getAnzSchluessel() > 0) {
                logger.info("ES wurde interagiert mit Object: {}", objectName);
                spielService.anzahlSchluesselVerringern(spielService.findeSpiel(lobbyID));
                logger.info("Anzahl Schluessel in Spiel {} betraegt {}",
                        lobbyID, spielService.findeSpiel(lobbyID).getAnzSchluessel());
                // Score von dem Spieler dessen name mitgegeben wurde erhoehen
                spielService.scoreErhoehen(spielService.getSpieler(lobbyID, spielerName), 5);
                logger.info("SpielerScore: {}", spielService.getSpieler(lobbyID, spielerName).getScore());
                return new SchluesselUpdate(objectName, spielService.findeSpiel(lobbyID).getAnzSchluessel(), position);
                // ... wenn nicht soll das Frontend den Warnungstext setzten
            } else {
                return new SchluesselUpdate("Warnung", spielService.findeSpiel(lobbyID).getAnzSchluessel(), position);

            }

        }

        return null;

    }

    // @MessageMapping("/topic/spiel/{lobbyID}/{objectName}")

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
