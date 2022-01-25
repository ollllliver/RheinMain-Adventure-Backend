package de.hsrm.mi.swt.rheinmainadventure;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.hsrm.mi.swt.rheinmainadventure.controller.BenutzerController;
import de.hsrm.mi.swt.rheinmainadventure.entities.Benutzer;
import de.hsrm.mi.swt.rheinmainadventure.repositories.IntBenutzerRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests für die Restschnittstelle BenutzerController
 */

 
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BenutzerRestTest {
   private static final String TESTBENUTZERNAME = "hopsi";
   private static final String TESTPASSWORT = "abcxyz";
   private static String TESTLOGINJSON;
   @Autowired
   BenutzerController benutzerController;
   @Autowired
   private MockMvc mockmvc;
   @Autowired
   private IntBenutzerRepo benutzerrepo;
 
   @BeforeAll
   public static void initAll() {
       ObjectNode json = JsonNodeFactory.instance.objectNode();
       json.put("benutzername", TESTBENUTZERNAME);
       json.put("passwort", TESTPASSWORT);
       TESTLOGINJSON = json.toString();
   }
 
   @Test
   void vorabcheck() {
       assertThat(benutzerController).isNotNull();
       assertThat(mockmvc).isNotNull();
   }
 
   @BeforeEach
   public void init() {
       benutzerrepo.deleteAll();
   }
 
 
   @Test
   @DisplayName("GET /api/benutzer liefert JsonListe")
   void benutzer_get() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       assertThat(benutzerrepo.count()).isEqualTo(1);
       mockmvc.perform(
                       get("/api/benutzer")
                               .contentType("application/json"))
               .andExpect(status().isOk());
   }
 
   @Test
   @DisplayName("POST /api/benutzer/register mit ok Formulardaten legt Benutzer an")
   void benutzer_neu_post_ok() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
   }
 
   @Test
   @DisplayName("POST /api/benutzer/register mit doppeltem Loginnamen geht nicht")
   void benutzer_neu_post_name_doppelt() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       assertThat(benutzerrepo.count()).isEqualTo(1);
 
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is4xxClientError());
   }
 
   @Test
   @DisplayName("POST /api/benutzer/register + /api/benutzer/login funktioniert")
   void benutzer_login() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       assertThat(benutzerrepo.count()).isEqualTo(1);
       Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
       assertThat(b).isNotNull();
 
       mockmvc.perform(
                       post("/api/benutzer/login")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
   }
 
   @Test
   @DisplayName("GET /api/benutzer/check checkt prüft ob Nutzer eingeloggt")
   void benutzer_check() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       assertThat(benutzerrepo.count()).isEqualTo(1);
       Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
       assertThat(b).isNotNull();
 
       mockmvc.perform(
                       post("/api/benutzer/login")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().isAccepted())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       HashMap<String, Object> sessionattr = new HashMap<String, Object>();
       sessionattr.put("loggedinBenutzername", b.getBenutzername());
 
       // ERFOLGREICH eingeloggt mit Sessionattribut gesetzt
       mockmvc.perform(
                       get("/api/benutzer/check").sessionAttrs(sessionattr)
                               .contentType("application/json"))
               .andExpect(status().isOk());
 
   }
 
   @Test
   @DisplayName("POST /api/benutzer/logout")
   void benutzer_logout() throws Exception {
       mockmvc.perform(
                       post("/api/benutzer/register")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       mockmvc.perform(
                       post("/api/benutzer/login")
                               .content(TESTLOGINJSON)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$.benutzername").value(TESTBENUTZERNAME));
 
       assertThat(benutzerrepo.count()).isEqualTo(1);
       Benutzer b = benutzerrepo.findByBenutzername(TESTBENUTZERNAME);
       assertThat(b).isNotNull();
       mockmvc.perform(
               post("/api/benutzer/logout")
                       .content(TESTLOGINJSON)
                       .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().is2xxSuccessful());
 
       mockmvc.perform(
                       get("/api/benutzer/check")
                               .contentType("application/json"))
               .andExpect(status().isNoContent());
 
   }
}
