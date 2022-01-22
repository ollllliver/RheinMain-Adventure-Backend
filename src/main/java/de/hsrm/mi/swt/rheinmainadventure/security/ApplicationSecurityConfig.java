package de.hsrm.mi.swt.rheinmainadventure.security;

import de.hsrm.mi.swt.rheinmainadventure.jwt.JwtTokenVerifier;
import de.hsrm.mi.swt.rheinmainadventure.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import de.hsrm.mi.swt.rheinmainadventure.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // generats csrf token, denies zugriff auf clientsid, needed if sent from client
//                .and()
                .csrf().disable()
                // weil jwttoken stateless, session wont be stored in database as it was without jwttokens
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //jwttoken filter
                // authenticationManager weil extends WebSecurity...
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager()))
                .addFilterAfter(new JwtTokenVerifier(), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers("/","index","/css/*","/js").permitAll() // auch ohne login verfuegbar
                    .antMatchers("/api/**").hasRole(ApplicationUserRole.BENUTZER.name())
                    // Diese Befehle werden in Benutzer2AdminController gesetzt
    //                .antMatchers(HttpMethod.DELETE,"/admin/api/**").hasAuthority(ADMIN_WRITE.getPermission())
    //                .antMatchers(HttpMethod.POST,"/admin/api/**").hasAuthority(ADMIN_WRITE.getPermission())
    //                .antMatchers(HttpMethod.PUT,"/admin/api/**").hasAuthority(ADMIN_WRITE.getPermission())
    //                .antMatchers("/admin/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name())

                    .anyRequest()
                    .authenticated();





//                // AB HIER ALLES EIGENTLICH AUF HTML SEITEN ALSO KANN DAS RAUS, trotzdem mal gut gesehen zu haben
//                .and()
//                // form based authentification
//                    .formLogin()// form based authentification
//                        // ist halt keine html sondern der Pfad... also HOWWWW
//                        .loginPage("/api/login")
//                        .permitAll()
//                        // einfach auf home zurueck
//                        .defaultSuccessUrl("/")
//                        .usernameParameter("benutzername")
//                        .passwordParameter("passwort")
//                .and()
//                // anstatt 30 Minuten fuer zwei Wochen remembered in SessionId
//                    .rememberMe().tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(17)).key("siebzehnsicherungen")
//                    .rememberMeParameter("remember-me")
//                .and()
//                    .logout()// wenn csrf an ==> logout ist POST request, sonst Logoutrequestmatcher new antpathrequestmatcher(logouturl, "GET")
//                        .logoutUrl("/api/logout")
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "GET")) // wenn csrf.disable() drin ist
//                        .clearAuthentication(true)
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID", "remember-me")
//                        .logoutSuccessUrl("/login");
                //.authenticationDetailsSource("/api/login");
                //.loginPage("/login").permitAll(); // sollte seite aus frontend sein, also eigentlich nicht benoetigt
//                .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }



    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }



//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails testUser = User.builder()
//                .username("test")
//                .password(passwordEncoder.encode("test"))
////                .roles(ApplicationUserRole.STUDENT.name())
//                .authorities(BENUTZER.getGrantedAuthorities())
//                .build();
//
//        UserDetails adminUser = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin"))
////                .roles(ApplicationUserRole.ADMIN.name())
//                .authorities(ADMIN.getGrantedAuthorities())
//                .build();
//
//        return new InMemoryUserDetailsManager(
//                testUser,
//                adminUser
//        );
//    }



















    //    @Autowired
//    MyUserDetailsService myUserDetailsService;
//
//    @Autowired
//    private JwtAuthorizationFilter jwtAuthorizationFilter;
//
//

//
//
//    @Configuration
//    public class Config {
//        @Bean
//        public RestTemplate restTemplate() {
//            return new RestTemplate();
//        }
//    }
//
//    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//
//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        // websecurity is above http security, also einstellungen in httpsecurity auf denselben pfad
//        // wie bspw. api/login .hasRole() in http waere ignoriert
//        // reihenfolge wichtig !
//        // deaktiviert benoetigte authentification fuer alles vor login (check,login,register)
//        webSecurity
//                .ignoring()
//                    .antMatchers( HttpMethod.GET,
//                            "/api/check"//,"/messagebroker", "/gamebroker"
//                    )
//                    .antMatchers(HttpMethod.POST,
//                            "/api/login", "/api/register"//,"/messagebroker", "/gamebroker"
//                    ) // deaktiviert security abfrage fuer posten auf login
//                    .antMatchers("/error")
//                    .antMatchers(HttpMethod.OPTIONS, "/**") // allow cors option calls
//                .and()
//                    .ignoring()
//                    .antMatchers("/h2/**/**");//Should not be in Production!
//    }
//
//
//
//
//    }
//
//    @Override
//    protected void configure(final HttpSecurity http) throws Exception{
//        // the more specific rules need to come first, followed by the more general ones
//
////        http.csrf().disable()
////                .authorizeRequests()
////                .antMatchers(HttpMethod.POST,"/api/login", "/api/register").permitAll() // deaktiviert security abfrage fuer posten auf login ,"/messagebroker", "/gamebroker"
////                .antMatchers( HttpMethod.GET ).permitAll() //,"/messagebroker", "/gamebroker"
////                //.antMatchers(HttpMethod.OPTIONS, "/**") // allow cors option calls
////            .and()
////                .csrf()
////                .ignoringAntMatchers("/h2/**/**");
//
//
//        http
////                .csrf().disable()//deaktiviert cross site request forgery
////                .headers()
////                //.frameOptions().sameOrigin() // CSRF
////                .httpStrictTransportSecurity().disable()
////            .and()
////                .sessionManagement()
////                //.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
////                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // zustandslose Rest-API ?
////            .and()
//                .authorizeRequests()
//                // hiernach alle Pfade festlegen die authorized sein sollen
//                .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENT.name())
//                .antMatchers("/messagebroker", "/gamebroker").permitAll() // Auch ohne login zugreifbar
//                .antMatchers(HttpMethod.GET, "/api/lobby/neu").permitAll()
//                //.antMatchers("/error").permitAll() // error geht immer, in websecurity festgelegt
//                .antMatchers("/messagebroker", "/gamebroker").permitAll()
//
//                .anyRequest().hasAnyAuthority()
////                .and()
////                .csrf()
////                .ignoringAntMatchers("/h2/**/**")
//            .and()
//                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
////                .csrf().disable();;
////                .anyRequest().authenticated();
//
////        http
////                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
////                .csrf().disable();
//
//
//        // um h2 console anzeigen zu lassen
//
//        http.headers().frameOptions().disable();
//        //http.csrf().ignoringAntMatchers("/h2","/h2/**","/h2/**/**"); // Hier sollte eigentlich abgefragt werden ob administrator...
//
//    }



}
