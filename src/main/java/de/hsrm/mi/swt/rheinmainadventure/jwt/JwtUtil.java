package de.hsrm.mi.swt.rheinmainadventure.jwt;

import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


/**
 * JwtUtil - simple Hilfsklasse zum erzeugen und Auslesen von JWTs
 * enthält (verwerflicher-, aber einfacherweise) auch den Signaturkey
 *
 * Benötigt io.jsonwebtoken Bibliothek, Dependencies in build.gradle:
 *
 *   implementation 'io.jsonwebtoken:jjwt-api:0.11.2'
 *   runtime 'io.jsonwebtoken:jjwt-impl:0.11.2'
 *   runtime 'io.jsonwebtoken:jjwt-jackson:0.11.2'
 */
@Component
public class JwtUtil {
    /*
     * Das ist DER JWT-Key zum Signieren und Überprüfen der JWTs
     * sollte natürlich "in real life" länger, binärer und konfigurierbarer sein -
     * hier zur Einfachheit und zum Experimentieren ein "simpler String"
     * (Mindestlänge für unseren Signatur-Algo HS256:  256 Bits / 32 Zeichen)
     */
    public final static String JWTKEYSTRING = "Dies ist der verschluesselungskey";



    final JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(JwtUtil.JWTKEYSTRING.getBytes())
            .build();


    /**
     * Einfaches JWT mit zwei vorgegebenen Claims erzeugen
     * @param loginname - Loginname des (authentifizierten) Benutzers
     * @param rollenname - Rolle des (authentifizierten) Benutzers
     * @return JWT-Token-String mit o.g. Claims, Ausgabedatum "jetzt" und mit JWTKEYSTRING signiert
     */
    public String bastelJwtToken(String loginname, String rollenname) {
        var claims = new HashMap<String, Object>();
        claims.put("benutzername", loginname);
        claims.put("rollen", rollenname);
        String token = Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(JWTKEYSTRING.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    /**
     * gibt gewünschten Claim-Wert aus übergebenem JWT zurück
     * @param token - signiertes JWT
     * @param claimname - auszulesender Claim
     * @return Wert des gewünschten Claims als String (null, falls claimname nicht dahanne)
     */
    public String extrahiereClaim(String token, String claimname) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.get(claimname, String.class);
    }
}
