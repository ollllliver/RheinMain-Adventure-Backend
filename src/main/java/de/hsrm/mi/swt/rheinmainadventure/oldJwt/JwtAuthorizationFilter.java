package de.hsrm.mi.swt.rheinmainadventure.oldJwt;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JwtAuthorizationFilter - untersuche HTTP-Anfrage nach "Authorization: Bearer ..."-Header
 * Falls vorhanden und JWT-Token korrekt, erfolgreichen Authentifzierung (und damit 
 * eingeloggten Benutzer) merken
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;
        
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("doFilterInternal: {} - {} = {}", request.getMethod(), request.getRequestURI(), request.getRequestURL());

        /*
         * Aus eingehender Anfrage 'Authorization'-Header extrahieren (sofern vorhanden, Groß-/Kleinschreibung im Header egal)
         */
        var authval = request.getHeader("authorization");
        if (authval != null) {
            /*
             * Authorization-Header-Wert (Format "Bearer xxxxxxxx") nach Whitespace splitten
             * falls Format falsch: HTTP-Status setzen und returnen
             */
            var authvalfields = authval.trim().split("\\s+");
            if (authvalfields.length != 2 || !authvalfields[0].equalsIgnoreCase("bearer")) {
                logger.error("doFilterInternal: Authorization: Bearer und/oder Token fehlt: {}", authval);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            /*
             * Zeichenfolge nach "Bearer" extrahieren, sollte JWT-Token sein
             */
            var token = authvalfields[1];
            try {
                /*
                 * JWT-Token-Claims 'benutzername' und 'rollen' extrahieren
                 */
                var loginname = jwtUtil.extrahiereClaim(token, "benutzername");
                var rolename = jwtUtil.extrahiereClaim(token, "rollen");

                /*
                 * UsernamePasswordAuthenticationToken aus loginname und Rolle zusammenbauen,
                 * dann Spring als (erfolgreiche) Authentifizierung übergeben
                 */
                Authentication authtok = new UsernamePasswordAuthenticationToken(loginname, null, 
                        List.of(new SimpleGrantedAuthority("ROLE_"+rolename.toUpperCase())));
                SecurityContextHolder.getContext().setAuthentication(authtok);
                logger.info("doFilterInternal: authentifiziert ok, authtoken {}", authtok);

            } catch (JwtException e) {
                /*
                 * Bei JWT-Formatfehler oder fehlerhafter Signatur HTTP-Status setzen und raus
                 */
                logger.error("doFilterInternal - Exception bei Token-Parserei {}", e.getMessage());
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }else{
            logger.info("Kein authorization header gefunden");
        }

        /*
         * Falls kein Authorization-Header vorhanden oder falls erfolgreich JWT-authentifiziert - 
         * Filterkette für den Request weiter laufen lassen
         */
        filterChain.doFilter(request, response);
    }   
}
