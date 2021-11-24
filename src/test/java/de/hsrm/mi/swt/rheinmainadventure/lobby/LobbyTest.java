package de.hsrm.mi.swt.rheinmainadventure.lobby;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest
public class LobbyTest {

    @Autowired
    LobbyService lobbyService;

    @Test
    public void testLobbyID(){


        // zu testende Benutzernamen
        // String lobbyID1 = lobbyService.GenerateLobbyID("User1");
        // String lobbyID2 = lobbyService.GenerateLobbyID("User1");
        // String lobbyID3 = lobbyService.GenerateLobbyID("");
        // String lobbyID4 = lobbyService.GenerateLobbyID("!aD   A*Säd+ +as D3ade2+ä     d1+dä as");
        // String lobbyID5 = lobbyService.GenerateLobbyID("x");


        // String[] idWerte = {lobbyID1, lobbyID2, lobbyID3, lobbyID4, lobbyID5};

        // HashSet<String> idSet = new HashSet<String>();

        // for(String id : idWerte){
        //     // jede ID ist zwischen 5 und 10 Zeichen lang
        //     assertTrue(id.length() > 5 && id.length() < 10);

        //     // jede ID ist einmalig
        //     assertTrue(idSet.add(id));
        // }
    }
    
}