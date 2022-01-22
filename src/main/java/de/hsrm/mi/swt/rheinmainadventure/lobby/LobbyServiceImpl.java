package de.hsrm.mi.swt.rheinmainadventure.lobby;

import de.hsrm.mi.swt.rheinmainadventure.entities.Level;
import de.hsrm.mi.swt.rheinmainadventure.messaging.LobbyMessage;
import de.hsrm.mi.swt.rheinmainadventure.messaging.NachrichtenCode;
import de.hsrm.mi.swt.rheinmainadventure.model.ChatNachricht;
import de.hsrm.mi.swt.rheinmainadventure.model.ChatNachricht.NachrichtenTyp;
import de.hsrm.mi.swt.rheinmainadventure.model.Spieler;
import de.hsrm.mi.swt.rheinmainadventure.spiel.LevelService;
import de.hsrm.mi.swt.rheinmainadventure.spiel.Spiel;
import de.hsrm.mi.swt.rheinmainadventure.spiel.SpielService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.*;

/**
 * Lobby Service für das verwalten aller Lobbys.
 */
@Service
@SessionAttributes(names = {"loggedinBenutzername"})
public class LobbyServiceImpl implements LobbyService {
    private static final String TOPICLOB = "/topic/lobby/";
    private static final String TOPICUEB = "/topic/lobby/uebersicht";
    private final Logger logger = LoggerFactory.getLogger(LobbyServiceImpl.class);
    /**
     * Liste aller Lobbyinstanzen.
     */
    private final ArrayList<Lobby> lobbys = new ArrayList<>();
    /**
     * Der Messagebroker wird hier durch dependencyInjection eingebunden. Über ihn
     * koennen Nachrichten ueber STOMP an die Subscriber gesendet werden
     */
    @Autowired
    private SimpMessagingTemplate broker;
    @Autowired
    private SpielService spielService;
    @Autowired
    private LevelService levelService;

    /**
     * Generiert eine einmalige Lobby-ID aus dem Namen des Spielers, kombiniert mit
     * einem Hashwert des aktuellen Zeitstempels
     *
     * @param benutzerName Der mitgegebene Name des Spielers
     * @return Gibt die generierte Lobby-ID als String zurueck
     */
    private String generateLobbyID(String benutzerName) {
        StringBuilder lobbyID = new StringBuilder();

        // Codesmell war da! Besser mit Stringbinder:
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < benutzerName.length(); i++) {
            char neuerChar = benutzerName.charAt(i);
            neuerChar += 1;
            bld.append(neuerChar);
        }

        // Benutzername verschoben um eine Stelle
        String verschobenerName = bld.toString();

        // Hash-Wert aus aktueller Zeit
        String aktZeit = java.time.LocalTime.now().toString();
        String zeitHashWert = String.valueOf(Math.abs(Integer.parseInt(aktZeit)));

        int zaehler = 0;

        // Kombination von Name und Zeit-Hashwert fuer Lobby-ID
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                if (zeitHashWert.length() > zaehler) {
                    lobbyID.append(zeitHashWert.charAt(zaehler));
                    zaehler++;
                }
            } else {
                if (verschobenerName.length() > zaehler
                        && Character.isLetterOrDigit(verschobenerName.charAt(zaehler))) {
                    lobbyID.append(verschobenerName.charAt(zaehler));
                    zaehler++;
                }
            }
        }

        return lobbyID.toString();
    }

    /**
     * Erstellt eine neue Lobby mit dem mitgegebenen Spieler(namen) als Host.
     *
     * @param spielerName des Lobby Erstellers
     * @return neu erstellte Lobby
     */
    @Override
    public Lobby lobbyErstellen(String spielerName) {
        // Hier wird eine neue Lobby erstellt. Der Host ist der Benutzer aus dem
        // Sessionscope.
        Spieler host = new Spieler(spielerName);
        host.setHost(true);
        ArrayList<Spieler> players = new ArrayList<>();
        String lobbyID = generateLobbyID(spielerName);

        List<Level> alleLevel = levelService.alleLevel();
        if (!levelService.alleLevel().isEmpty()) {
            Lobby lobby = new Lobby(lobbyID, players, host, alleLevel.get(0));
            starteTimeout(lobby);
            lobbys.add(lobby);

            broker.convertAndSend(TOPICUEB, new LobbyMessage(NachrichtenCode.NEUE_LOBBY, false, lobbyID));
            return lobby;
        } else {
            throw new NoSuchElementException("es gibt kein level in der Datenbank.");
        }
    }

    /**
     * Funktion für verlassen der Lobby, entfernt spielernamen aus der
     * ArrayList<Spieler>
     *
     * @param id          lobby ID
     * @param spielerName spieler der die lobby verlassen will
     * @return LobbyMessage dass eine spieler die lobby verlassen hat
     */
    @Override
    public LobbyMessage spielerVerlaesstLobby(String id, String spielerName) {
        // Spieler wird gesucht aus der aktuellenTeilnehmerList...
        int teilnehmerzahl = getLobbyById(id).getTeilnehmerliste().size();
        for (int i = 0; i < teilnehmerzahl; i++) {
            Spieler currSpieler = getLobbyById(id).getTeilnehmerliste().get(i);
            if (currSpieler.getName().equals(spielerName)) {
                // ... und entfernt
                // wenn lobby leer ist wird sie geschlossen
                if (getLobbyById(id).getTeilnehmerliste().size() == 1) {

                    lobbys.remove(getLobbyById(id));
                    broker.convertAndSend(TOPICUEB, new LobbyMessage(NachrichtenCode.LOBBY_ENTFERNT, false));
                } else {

                    getLobbyById(id).getTeilnehmerliste().remove(currSpieler);

                    // wenn der spieler der Host war wird der Status weitergegeben
                    if (spielerName.equals(getLobbyById(id).getHost().getName())) {

                        int size = getLobbyById(id).getTeilnehmerliste().size();
                        double index = Math.floor(Math.random() * size);

                        Spieler neuerHost = getLobbyById(id).getTeilnehmerliste().get((int) index);

                        neuerHost.setHost(true);
                        getLobbyById(id).setHost(neuerHost);
                    }
                }
                break;
            }
        }
        broker.convertAndSend(TOPICLOB + id, new LobbyMessage(NachrichtenCode.MITSPIELER_VERLAESST, false));
        broker.convertAndSend(TOPICLOB + id + "/chat", new ChatNachricht(NachrichtenTyp.LEAVE, "", spielerName));
        return new LobbyMessage(NachrichtenCode.MITSPIELER_VERLAESST, false);

    }

    /**
     * Timeout Funktion für Lobbs die nach 10 Minuten Thread-Safe eine Lobby
     * schliesst
     *
     * @param lobby Lobby dessen Timeout gestartet wird.
     */
    private void starteTimeout(Lobby lobby) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            public void run() {
                if (!lobby.getIstGestartet()) {

                    // per STOMP Service allen Nutzern die auf diese Aktuelle lobbyID
                    // Subscribed sind eine Fehlermeldung per Publish senden und im Frontend
                    // abfangen.
                    // @Chand das wuerde jetzt so gehen:
                    broker.convertAndSend(TOPICUEB + lobby.getlobbyID(),
                            new LobbyMessage(NachrichtenCode.LOBBYZEIT_ABGELAUFEN, true));
                    // Das sendet an alle, die in der Lobby eingeschrieben sind die message
                    // LOBBYZEIT_ABGELAUFEN

                    lobbys.remove(lobby);
                    broker.convertAndSend(TOPICUEB, new LobbyMessage(NachrichtenCode.LOBBY_ENTFERNT, false));

                }
            }

        };
        // timer.schedule(task, 15 * 1000); // für Testing auf 5 Sekunden setzen.
        timer.schedule(task, 10 * 60 * 1000L);
    }

    /**
     * Startet den Countdown fuer den Spielstart einer Lobby - setzt die
     * IstGestartet Variable nach 10 Sekunden auf true.
     *
     * @param lobbyId ID der Lobby dessen Countdown gestartet wird.
     * @return gibt eine LobbyMessage zurueck die aussagt das der Countdown
     * gestartet worden ist.
     */
    @Override
    public LobbyMessage starteCountdown(String lobbyId) {
        broker.convertAndSend(TOPICLOB + lobbyId,
                new LobbyMessage(NachrichtenCode.COUNTDOWN_GESTARTET, false, "Sekunden=10"));

        Lobby lobby = getLobbyById(lobbyId);
        if (!lobby.getIstGestartet()) {
            lobby.setIstGestartet(true);
            spielService.starteSpiel(lobby);
        }

        return new LobbyMessage(NachrichtenCode.COUNTDOWN_GESTARTET, false, "Sekunden=10");
    }

    /**
     * Gibt ALLE aktuellen Lobbs als Array zurueck.
     */
    public ArrayList<Lobby> getLobbys() {
        logger.info("Anzahl lobbys: {}", this.lobbys.size());
        return this.lobbys;
        // Bsp.:
        // [{"lobbyID":"4l1a7y16","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0},
        // {"lobbyID":"1r4a4l08","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}],
        // {"lobbyID":"5r4l2P44","playerList":[{"id":0,"name":"Player1"}],"host":{"id":0,"name":"Player1"},"istVoll":false,"istGestartet":false,"spielerlimit":0}]
    }

    /**
     * Gibt die eine Lobby mit der übergebenen id zurück.
     *
     * @param id ist die Lobby ID der gewünschten Lobby
     * @return Lobby mit der mitgegebenen ID
     */
    public Lobby getLobbyById(String id) {
        // Gibt die Lobby mit uebergebener ID zurueck. Wenn nicht vorhanden, dann return
        // 0.
        for (Lobby currLobby : lobbys) {
            if (currLobby.getlobbyID().equals(id)) {
                return currLobby;
            }
        }
        return null;
    }

    /**
     * Fuegt den Sessionspieler der mitgegebenen Lobby (ID) ueber die
     * nutzerHinzufuegen() Funktion der Lobby Klasse hinzu.
     *
     * @param id          mitgegebene Lobby-ID
     * @param spielername Der mitgegebene Name des Spielers
     * @return Gibt eine LobbyMessage mit passendem NachrichtenCode, sowie
     * Erfolgsstatus zurueck
     */
    @Override
    public LobbyMessage joinLobbybyId(String id, String spielername) {
        // Eigentlich ohne Spieler. In der Lobby.nutzerHinzufuegen() Methode muss der
        // Spieler aus der SessionScope geholt werden
        logger.info("{} will der Lobby {} joinen", spielername, id);

        Lobby currLobby = getLobbyById(id);
        if (currLobby == null) {
            return new LobbyMessage(NachrichtenCode.LOBBY_NICHT_GEFUNDEN, true);
        }
        // ueberpruefen, ob Spieler bereits in der Lobby ist
        if (!currLobby.getTeilnehmerliste().contains(new Spieler(spielername))) {

            // Wenn Lobby nicht voll oder im Spiel (oder Spieler nicht schon drinnen), wird
            // der Spieler in die Teilnehmerliste aufgenommen
            // und es wird gegebenenfalls istVoll angepasst.
            if (currLobby.getIstGestartet()) {
                return new LobbyMessage(NachrichtenCode.LOBBY_GESTARTET, true);
            } else if (currLobby.getIstVoll()) {
                return new LobbyMessage(NachrichtenCode.LOBBY_VOLL, true);
            } else {
                Spieler spieler;
                if (currLobby.getHost().getName().equals(spielername)) {
                    spieler = currLobby.getHost();
                } else {
                    spieler = new Spieler(spielername);
                }
                currLobby.getTeilnehmerliste().add(spieler);
                currLobby.setIstVoll((currLobby.getTeilnehmerliste().size() >= currLobby.getSpielerlimit()));
                broker.convertAndSend(TOPICLOB + id,
                        new LobbyMessage(NachrichtenCode.NEUER_MITSPIELER, false, currLobby.getlobbyID()));
                broker.convertAndSend(TOPICLOB + id + "/chat", new ChatNachricht(NachrichtenTyp.JOIN, "", spielername));
                return new LobbyMessage(NachrichtenCode.ERFOLGREICH_BEIGETRETEN, false, currLobby.getlobbyID());
            }
        }
        return new LobbyMessage(NachrichtenCode.SCHON_BEIGETRETEN, false, currLobby.getlobbyID());
    }

    /**
     * Laesst einen Spieler einer zufaelligen freien Lobby beitreten
     *
     * @param spielername Name des spielers der einer zufaelligen Lobby beitritt
     * @retun Gibt einene LobbyMessage mit dem ergebnis des beitretens zurueck
     */
    @Override
    public LobbyMessage lobbyBeitretenZufaellig(String spielername) {
        Lobby tempLobby = null;
        for (Lobby currLobby : lobbys) {
            if (!currLobby.getIstGestartet() && !currLobby.getIstVoll() && !currLobby.getIstPrivat()) {
                tempLobby = currLobby;
                break;
            }
        }
        if (tempLobby != null) {
            return joinLobbybyId(tempLobby.getlobbyID(), spielername);
        } else {
            return new LobbyMessage(NachrichtenCode.KEINE_LOBBY_FREI, true);
        }

    }

    /**
     * Überprüft, ob der anfragende Spiler Änderungen an der Lobby vornehmen darf
     * (wenn man Host ist) und setzt dann das spielerlimit der Lobby auf den
     * mitgegebenen Wert.
     *
     * @param id           der Lobby, um die es geht
     * @param spielerlimit neuer Wert für das Spielerlimit
     * @return LobbyMessage mit Information über den Ausgang der Anfrage
     */
    @Override
    public LobbyMessage setSpielerlimit(String id, int spielerlimit, String spielerName) {
        if (getLobbyById(id).getHost().getName().equals(spielerName)) {
            getLobbyById(id).setSpielerlimit(spielerlimit);
            LobbyMessage res = new LobbyMessage(NachrichtenCode.NEUE_EINSTELLUNGEN, false);
            broker.convertAndSend(TOPICLOB + id, res);
            return res;
        }
        return new LobbyMessage(NachrichtenCode.KEINE_BERECHTIGUNG, true);
    }

    /**
     * Überprüft, ob anfragende Spiler Änderungen an der Lobby vornehmen darf
     * (wenn man Host ist) und setzt dann die Flag istPrivat um.
     *
     * @param id          der Lobby, um die es geht
     * @param istPrivat   Boolean wie die Lobby gesetzt werden soll
     * @param spielerName des anfragenden Spielers
     * @return LobbyMessage mit Information über den Ausgang der Anfrage
     */
    @Override
    public LobbyMessage setPrivacy(String id, Boolean istPrivat, String spielerName) {
        if (getLobbyById(id).getHost().getName().equals(spielerName)) {
            getLobbyById(id).setIstPrivat(istPrivat);
            LobbyMessage res = new LobbyMessage(NachrichtenCode.NEUE_EINSTELLUNGEN, false);
            broker.convertAndSend(TOPICLOB + id, res);
            return res;
        }
        return new LobbyMessage(NachrichtenCode.KEINE_BERECHTIGUNG, true);
    }

    /**
     * Überprüft, ob anfragende Spiler einen anderen zum Host ernennen darf (wenn
     * man Host ist,
     * darf man den Host weitergeben) und macht das dann. Der Anfrager its danach
     * kein Host mehr.
     *
     * @param id          der Lobby, um die es geht
     * @param host        der Spieler, der aktuell noch Host der Lobby ist
     * @param spielerName des Spielers, der Host werden soll
     * @return LobbyMessage mit Information über den Ausgang der Anfrage
     */
    @Override
    public LobbyMessage setHost(String id, Spieler host, String spielerName) {
        // Da host ja nicht wirklich das Objekt ist, da in der Teilnehmerliste ist,
        // müssen wir es erst mit dem gemeinten Objekt ersetzten
        host = getLobbyById(id).getTeilnehmerliste().get(getLobbyById(id).getTeilnehmerliste().indexOf(host));
        if (getLobbyById(id).getHost().getName().equals(spielerName)) {
            getLobbyById(id).setHost(host);
            LobbyMessage res = new LobbyMessage(NachrichtenCode.NEUE_EINSTELLUNGEN, false);
            broker.convertAndSend(TOPICLOB + id, res);
            return res;
        }
        return new LobbyMessage(NachrichtenCode.KEINE_BERECHTIGUNG, true);
    }

    /**
     * Überprüft, ob der entfernende Spiler den zu entfernenden Spieler entfernen darf (wenn man Host ist,
     * darf man andere entfernen) und macht das dann.
     *
     * @param id                  die ID der Lobby, aus der ein Spieler entfernt werden soll.
     * @param zuEntfernendSpieler zu entfernender Spieler
     * @param spielerName         Spieler, der die Anfrage stellt
     * @return LobbyMessage mit Information über den Ausgang der Anfrage
     */
    @Override
    public LobbyMessage removeSpieler(String id, Spieler zuEntfernendSpieler, String spielerName) {
        Lobby lobby = getLobbyById(id);
        // nur der Host darf machen:
        if (lobby.getHost().getName().equals(spielerName) && !lobby.getHost().equals(zuEntfernendSpieler)) {
            List<Spieler> teilnehmerliste = lobby.getTeilnehmerliste();
            if (teilnehmerliste.contains(zuEntfernendSpieler)) {
                teilnehmerliste.remove(zuEntfernendSpieler);
                LobbyMessage res = new LobbyMessage(NachrichtenCode.MITSPIELER_VERLAESST, false, zuEntfernendSpieler.getName());
                broker.convertAndSend(TOPICLOB + id, res);
                return res;
            }
        }
        return new LobbyMessage(NachrichtenCode.KEINE_BERECHTIGUNG, true);
    }

    /**
     * Wechselt nach beenden des Spiels zurück in die lobby Ansicht
     *
     * @param lobbyId Die id der Lobby zu der gewechselt werden soll
     * @return LobbyMessage mit Information über den Ausgang der Anfrage
     */
    @Override
    public LobbyMessage zurueckZurLobby(String lobbyId) {

        Lobby lobby = getLobbyById(lobbyId);
        Spiel spiel = spielService.getSpielByLobbyId(lobbyId);
        spielService.alleSpiele().remove(spiel);
        logger.info("Spiel beendent: {}", spiel);
        lobby.setIstGestartet(false);

        return new LobbyMessage(NachrichtenCode.BEENDE_SPIEL, false, "Spiel beendet. Kehre zurück zur Lobby");
    }

    @Override
    public LobbyMessage setLevel(String lobbyId, Long levelId, String spielerName) {
        // erst mal das Level durch die ID bekommen:
        Optional<Level> neuesLevelOpt = levelService.getLevel(levelId);
        if (neuesLevelOpt.isPresent() && getLobbyById(lobbyId).getHost().getName().equals(spielerName)) {
            getLobbyById(lobbyId).setGewaehlteKarte(neuesLevelOpt.get());
            LobbyMessage res = new LobbyMessage(NachrichtenCode.NEUE_EINSTELLUNGEN, false);
            broker.convertAndSend(TOPICLOB + lobbyId, res);
            return res;
        }
        return new LobbyMessage(NachrichtenCode.KEINE_BERECHTIGUNG, true);
    }
}
