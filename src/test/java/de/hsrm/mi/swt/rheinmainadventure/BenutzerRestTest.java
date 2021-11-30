package de.hsrm.mi.swt.rheinmainadventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests für die Restschnittstelle BenutzerController
 */

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BenutzerRestTest {
    final String TESTBENUTZERNAME = "hopsi";
    final String TESTPASSWORT = "abcxyz";

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private IntBenutzerRepo benutzerrepo;

    @Test
    void vorabcheck() {
        assertThat(benutzerrepo).isNotNull();
        assertThat(mockmvc).isNotNull();
    }

    @Test
    @DisplayName("GET /api/benutzer liefert JsonListe")
    void benutzer_get() throws Exception {
        mockmvc.perform(
                        get("/api/benutzer")
                                .contentType("application/json"))
                                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/benutzer/register mit ok Formulardaten legt Benutzer an")
    void benutzer_neu_post_ok() throws Exception {
        // Eintrag anlegen

        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(model().hasNoErrors());

        Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
        assertThat(b).isNotNull();
        assertThat(b.getBenutzername()).isEqualTo(TESTBENUTZERNAME);
        assertThat(b.getPasswort()).isEqualTo(TESTPASSWORT);
    }

    @Test
    @DisplayName("POST /api/benutzer/register mit falschen Formulardaten")
    void benutzer_neu_post_falsch() throws Exception {
        final String ZUKURZESPASSWORT = "xy";

        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", "")
                                .param("passwort", ZUKURZESPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /api/benutzer/register mit doppeltem Loginnamen geht nicht")
    void benutzer_neu_post_name_doppelt() throws Exception {
        // Eintrag anlegen

        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors());

        assertThat(benutzerrepo.count()).isEqualTo(1);

        mockmvc.perform(
                        post("/api/benutzer")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /api/benutzer/register + /api/benutzer/login funktioniert")
    void benutzer_login() throws Exception {
        // Eintrag anlegen

        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors());

        assertThat(benutzerrepo.count()).isEqualTo(1);
        Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
        assertThat(b).isNotNull();

        mockmvc.perform(
                        post("/api/benutzer/login")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasErrors())
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("GET /api/benutzer/check checkt prüft ob Nutzer eingeloggt")
    void benutzer_check() throws Exception {

        // Eintrag anlegen
        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors());

        assertThat(benutzerrepo.count()).isEqualTo(1);
        Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
        assertThat(b).isNotNull();

        // Eintrag prüfen
        mockmvc.perform(
                        post("/api/benutzer/login")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isAccepted());

        // ERFOLGREICH eingeloggt mit Sessionattribut gesetzt
        mockmvc.perform(
                        get("/api/benutzer/check")
                                .contentType("application/json"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("POST /api/benutzer/logout")
    void benutzer_logout() throws Exception {
        // Eintrag anlegen

        benutzerrepo.deleteAll();

        mockmvc.perform(
                        post("/api/benutzer/register")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors());

        assertThat(benutzerrepo.count()).isEqualTo(1);
        Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
        assertThat(b).isNotNull();

        mockmvc.perform(
                        post("/api/benutzer/login")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isAccepted());

        mockmvc.perform(
                        post("/api/benutzer/logout")
                                .param("benutzername", TESTBENUTZERNAME)
                                .param("passwort", TESTPASSWORT)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(model().hasNoErrors())
                .andExpect(status().isAccepted());
    }

}