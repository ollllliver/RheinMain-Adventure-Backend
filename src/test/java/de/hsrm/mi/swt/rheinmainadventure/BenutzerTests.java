package de.hsrm.mi.swt.rheinmainadventure;

import de.hsrm.mi.swt.rheinmainadventure.benutzer.BenutzerService;
import de.hsrm.mi.swt.rheinmainadventure.benutzer.BenutzerServiceImpl;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests für den BenutzerService
 */

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BenutzerTests {
    final String TESTLOGINNAME = "jockel";
    final String TESTPASSWORT = "supergeheimesjockelpasswort";
    @Autowired
    private BenutzerService benutzerService;
    @Autowired
    private IntBenutzerRepo benutzerrepo;

    @Test
    void vorabcheck() {
        assertThat(BenutzerService.class).isInterface();
        assertThat(benutzerService).isNotNull();
        assertThat(BenutzerService.class).isInterface();
        assertThat(benutzerService)
                .isNotNull()
                .isInstanceOf(BenutzerServiceImpl.class);
    }

    @Test
    @DisplayName("Benutzer-Entity ok")
    void benutzer_ok() {

        Benutzer ben = new Benutzer();
        ben.setBenutzername(TESTLOGINNAME);
        ben.setPasswort(TESTPASSWORT);
        benutzerService.registriereBenutzer(ben);
        assertThat(benutzerService.findeBenutzer(TESTLOGINNAME).getBenutzername()).isEqualTo(TESTLOGINNAME);
    }

    @Test
    @DisplayName("BenutzerService registrieren Neuuser")
    void benutzer_persist() {
        final Benutzer neuuser = new Benutzer();
        neuuser.setBenutzername(TESTLOGINNAME);
        neuuser.setPasswort(TESTPASSWORT);

        benutzerrepo.deleteAll();

        final Benutzer managed = benutzerService.registriereBenutzer(neuuser);
        assertThat(managed).isEqualTo(neuuser);

        assertThat(benutzerrepo.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("BenutzerRepo findByBenutzername)")
    void benutzer_loginname_findByLoginname() {
        final int ANZAHL = 5;

        benutzerrepo.deleteAll();

        for (int i = 0; i < ANZAHL; i++) {
            final Benutzer u1 = new Benutzer();
            u1.setBenutzername(TESTLOGINNAME + i);
            u1.setPasswort(TESTPASSWORT + i);
            benutzerrepo.save(u1);
            System.out.println(benutzerrepo.findAll());
        }
        assertThat(benutzerrepo.count()).isEqualTo(ANZAHL);

        for (int i = 0; i < ANZAHL; i++) {
            Benutzer fund = benutzerrepo.findByBenutzername(TESTLOGINNAME + i);
            assertThat(fund.getPasswort()).isEqualTo(TESTPASSWORT + i);
        }
    }

    @Test
    @DisplayName("BenutzerRepo findByLoginname() Fehlanzeige")
    void benutzer_loginname_findByLoginname_fehlanzeige() {
        benutzerrepo.deleteAll();

        final Benutzer u1 = new Benutzer();
        u1.setBenutzername(TESTLOGINNAME);
        u1.setPasswort(TESTPASSWORT);
        benutzerrepo.save(u1);

        assertThat(benutzerrepo.count()).isEqualTo(1);

        Benutzer b = benutzerrepo.findByBenutzername("gibtsnicht");
        assertThat(b).isNull();
    }

    @Test
    @DisplayName("BenutzerService pruefeLogin() nutzt DB")
    void benutzerservice_pruefelogin_db() {
        final Benutzer neuuser = new Benutzer();
        neuuser.setBenutzername(TESTLOGINNAME);
        neuuser.setPasswort(TESTPASSWORT);

        benutzerrepo.deleteAll();

        final Benutzer managed = benutzerService.registriereBenutzer(neuuser);
        assertThat(managed).isEqualTo(neuuser);
        assertThat(benutzerrepo.count()).isEqualTo(1);

        assertThat(benutzerService.pruefeLogin(TESTLOGINNAME, TESTPASSWORT)).isTrue();
    }

    @Test
    @DisplayName("Benutzerservice checkLogin() ok")
    void benutzerservice_checkLogin_ok() {
        Benutzer ben = new Benutzer();
        ben.setBenutzername("willi");
        ben.setPasswort("willi5");
        Benutzer ben2 = new Benutzer();
        ben2.setBenutzername("schnorchelnasenbaer");
        ben2.setPasswort("schnorchelnasenbaer19");
        benutzerrepo.save(ben);
        benutzerrepo.save(ben2);
        assertThat(benutzerService.pruefeLogin("willi", "willi5")).isTrue();
        assertThat(benutzerService.pruefeLogin("schnorchelnasenbaer", "schnorchelnasenbaer19")).isTrue();
    }

    @Test
    @DisplayName("BenutzerRepo doppelte Loginnamen verboten)")
    void benutzer_loginname_eindeutig() {
        final Benutzer u1 = new Benutzer();
        u1.setBenutzername(TESTLOGINNAME);
        u1.setPasswort(TESTPASSWORT);

        benutzerrepo.deleteAll();

        final Benutzer managed1 = benutzerrepo.save(u1);
        assertThat(managed1).isEqualTo(u1);

        final Benutzer u2 = new Benutzer();
        u2.setBenutzername(TESTLOGINNAME);
        u2.setPasswort("anderespasswort");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            benutzerrepo.save(u2);
        });

        assertThat(benutzerrepo.count()).isEqualTo(1);
    }


}
