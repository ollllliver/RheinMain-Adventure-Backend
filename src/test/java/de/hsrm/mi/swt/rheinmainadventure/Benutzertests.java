package de.hsrm.mi.swt.rheinmainadventure;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests für den BenutzerService
 */

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class Benutzertests {
    @Autowired
    private BenutzerService benutzerService;

    @Autowired
    private IntBenutzerRepo benutzerrepo;


    final String TESTLOGINNAME = "jockel";
    final String TESTPASSWORT = "supergeheimesjockelpasswort";


    @Test
    public void vorabcheck() {
        assertThat(BenutzerService.class).isInterface();
        assertThat(benutzerService).isNotNull();
        assertThat(BenutzerService.class).isInterface();
        assertThat(benutzerService).isNotNull();
        assertThat(benutzerService).isInstanceOf(BenutzerServiceImpl.class);
    }

    @Test
    @DisplayName("Benutzer-Entity ok")
    public void benutzer_ok() {

        Benutzer ben = new Benutzer();
        ben.setBenutzername(TESTLOGINNAME);
        ben.setPasswort(TESTPASSWORT);
        final Benutzer managed = benutzerService.registriereBenutzer(ben);
        assertThat(benutzerService.findeBenutzer(TESTLOGINNAME).getBenutzername()).isEqualTo(TESTLOGINNAME);
    }

    @Test
    @DisplayName("BenutzerService registrieren Neuuser")
    public void benutzer_persist() {
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
    public void benutzer_loginname_findByLoginname() {
        final int ANZAHL = 5;

        benutzerrepo.deleteAll();

        for (int i=0; i < ANZAHL; i++) {
            final Benutzer u1 = new Benutzer();
            u1.setBenutzername(TESTLOGINNAME+i);
            u1.setPasswort(TESTPASSWORT+i);
            benutzerrepo.save(u1);
        }
        assertThat(benutzerrepo.count()).isEqualTo(ANZAHL);

        for (int i=0; i < ANZAHL; i++) {
            Benutzer fund = benutzerrepo.findByBenutzername(TESTLOGINNAME+i);
            assertThat(fund.getPasswort()).isEqualTo(TESTPASSWORT+i);
        }
    }

    @Test
    @DisplayName("BenutzerRepo findByLoginname() Fehlanzeige")
    public void benutzer_loginname_findByLoginname_fehlanzeige() {
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
    public void benutzerservice_pruefelogin_db() {
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
    public void benutzerservice_checkLogin_ok() {
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
    public void benutzer_loginname_eindeutig() {
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
            Benutzer managed2 = benutzerrepo.save(u2);
            assertThat(managed2).isEqualTo(u2);
        });

        assertThat(benutzerrepo.count()).isEqualTo(1);
    }





}
