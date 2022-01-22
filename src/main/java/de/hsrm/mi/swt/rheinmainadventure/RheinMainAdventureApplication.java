package de.hsrm.mi.swt.rheinmainadventure;

import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // spring security https://www.baeldung.com/spring-boot-security-autoconfiguration
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
@EnableJpaRepositories(basePackageClasses = IntBenutzerRepo.class)
public class RheinMainAdventureApplication {

    public static void main(String[] args) {
        SpringApplication.run(RheinMainAdventureApplication.class, args);
    }

}
