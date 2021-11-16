package de.hsrm.mi.swt.rheinmainadventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RheinMainAdventureApplication {
    private LobbyManager lobbyManager = new LobbyManager();

    public static void main(String[] args) {
        SpringApplication.run(RheinMainAdventureApplication.class, args);
    }

}
