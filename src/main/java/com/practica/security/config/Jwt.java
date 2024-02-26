package com.practica.security.config;

import com.practica.security.model.User;
import com.practica.security.service.UserService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;


@Component
public class Jwt {
    @Value("${app.security.jwt.secret}")
    private String key;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    @Value("${app.security.jwt.ttlMillis}")
    private long ttlMillis;

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory
            .getLogger(Jwt.class);

    /**
     * Create a new token.
     *
     * @param id
     * @param subject
     * @return
     */
    public String create(String id, String subject,String email) {

        // The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //  sign JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //  set the JWT Claims
        JwtBuilder builder = Jwts
                .builder().setId(id)
                .setIssuedAt(now
                ).setSubject(subject)
                .setIssuer(issuer)
                .claim("email",email)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    /**
     * Method to validate and read the JWT
     *
     * @param jwt
     * @return
     */
    public String getValue(String jwt) {

        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                .parseClaimsJws(jwt).getBody();

        return claims.getSubject();
    }

    public User findCurrentUser(String jwtToken) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(jwtToken)
                    .getBody();

            String email = claims.get("email", String.class);
            return userService.findByEmail(email).withToken(jwtToken);
        } catch (ExpiredJwtException ex) {
            // Token expirado
            throw new Exception(ex.getMessage());
        } catch (JwtException ex) {
            // Otro tipo de error JWT
            throw new Exception(ex.getMessage());
        } catch (Exception ex) {
            // Otros errores
            throw new Exception(ex.getMessage());
        }
    }

    public ResponseEntity<String> getKey(String jwt) throws Exception {
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(jwt).getBody();
            return ResponseEntity.ok(claims.getId());
        } catch (ExpiredJwtException ex) {
            // Token expirado
            throw new Exception("Token invalid or expired");
        } catch (JwtException ex) {
            // Otro tipo de error JWT
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación");
        } catch (Exception ex) {
            // Otros errores
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación general");
        }
    }
}
