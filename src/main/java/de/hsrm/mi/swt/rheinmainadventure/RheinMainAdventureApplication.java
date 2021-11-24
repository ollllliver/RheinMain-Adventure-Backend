package de.hsrm.mi.swt.rheinmainadventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
public class RheinMainAdventureApplication {

    public static void main(String[] args) {
        SpringApplication.run(RheinMainAdventureApplication.class, args);
    }

}
