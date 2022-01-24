package de.hsrm.mi.swt.rheinmainadventure.security;

import de.hsrm.mi.swt.rheinmainadventure.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // standard encoder (delegating)
//    String passwordForEncode = "test";
//    Map<String, PasswordEncoder> encoders = new HashMap<>();
//    encoders.put(idForEncode, new BCryptPasswordEncoder());
//    encoders.put("noop", NoOpPasswordEncoder.getInstance());
//    encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
//    encoders.put("scrypt", new SCryptPasswordEncoder());
//    encoders.put("sha256", new StandardPasswordEncoder());



    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;


    @Bean
    PasswordEncoder passwordEncoder() {

        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
//        return new SecurityEvaluationContextExtension();
//    }

    @Configuration
    public class Config {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        // websecurity is above http security, also einstellungen in httpsecurity auf denselben pfad
        // wie bspw. api/login .hasRole() in http waere ignoriert
        webSecurity
                .ignoring()
                .antMatchers(
                        HttpMethod.POST,
                        "/api/login"
                ) // deaktiviert security abfrage fuer posten auf login
                .antMatchers(
                        HttpMethod.POST,
                        "/api/register"
                )
                .antMatchers(
                        HttpMethod.GET,
                        "/api/check"
                )
                //.antMatchers(HttpMethod.OPTIONS, "/**") // allow cors option calls
                .and()
                .ignoring()
//                .antMatchers(
//                        HttpMethod.GET,
//                        "/" //Other Stuff You want to Ignore
//                )
                .and()
                .ignoring()
                .antMatchers("/h2/**/**");//Should not be in Production!
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception{
        // the more specific rules need to come first, followed by the more general ones

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
                .authorizeRequests()
                .anyRequest().authenticated();

        http
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);


//        http
//                .antMatcher("/api/**") // beschraenkt nur auf Pfade unter /api/
//                .csrf().disable()
//                .authorizeRequests()
////                .antMatchers("/admin/**").hasRole("ADMIN")
////                .antMatchers("/anonymous*").anonymous()
//                .antMatchers("/api/login").permitAll()
//                .antMatchers("/api/benutzer/login").permitAll()
////                .antMatchers("/login*").permitAll()
////                .antMatchers("/api").permitAll()
//                //.antMatchers("/api/benutzer").hasAnyRole("ADMIN","BENUTZER")
//                .antMatchers("/api/benutzer").permitAll()
//                .antMatchers("/api/benutzer/**").hasAnyRole("ADMIN","BENUTZER")
//                .antMatchers("/messagebroker").permitAll() // richtiger Pfad ?
////                .anyRequest().authenticated() // nur authenticated user koennen zugreifen
//                //.anyRequest().denyAll() // alle anderen requests blocken
//                .and()
//                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // keine security sessions fuer zustandloses rest apis (anders als bei z.B. WebMVC)
//                //.and()
//                .formLogin()
//                .usernameParameter("benutzername")
//                .passwordParameter("passwort")
//                //.loginPage("/login.html")
//                .loginProcessingUrl("/api/benutzer/login")
//                //.defaultSuccessUrl("/api/benutzer", true) // in Frontend auf /home setzen, also muss hier nicht gesetzt werden ?!
//                //.failureUrl("/")
//                //.failureUrl("/login.html?error=true")
//                //.failureHandler(authentificationFailureHandler())
//                .and()
//                .logout()
//                .logoutUrl("/api/logout")
//                .deleteCookies("JSESSIONID");
                //.logoutSuccessHandler(logoutSuccessHandler());

        //.antMatchers("/login").permitAll()
        // Zugriff auf h2
        //.antMatchers("/**").permitAll();


        //.antMatchers("/h2/**").hasRole("ADMIN");
        //.antMatchers("/**/**/**").hasRole("ADMIN");
//        .antMatchers("/login").hasAnyRole("BESUCHER","BENUTZER","ADMIN")
//        //.antMatchers("/landingpage").hasRole("USER")
//        .antMatchers("/**").hasRole("ADMIN")
//        .antMatchers("/**/**").hasRole("ADMIN")
//        .and().formLogin();


        // um h2 console anzeigen zu lassen

        http.headers().frameOptions().disable();

    }



}
