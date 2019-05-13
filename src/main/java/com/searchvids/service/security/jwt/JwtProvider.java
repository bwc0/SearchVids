package com.searchvids.service.security.jwt;

import com.searchvids.service.security.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.jwtExpiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration*1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException se) {
            logger.error("Invalid JWT signature -> Message: {} ", se);
        } catch (MalformedJwtException mje) {
            logger.error("Invalid JWT token -> Message: {} ", mje);
        } catch (ExpiredJwtException eje) {
            logger.error("Expired JWT token -> Message: {} ", eje);
        } catch (UnsupportedJwtException uje) {
            logger.error("Unsupport JWT token -> Message: {} ", uje);
        } catch (IllegalArgumentException iae) {
            logger.error("JWT claims string empty -> Message {} ", iae);
        }

        return false;
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}
