package com.studygroup.group.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:your-secret-key-that-is-at-least-256-bits-long-for-HS256-algorithm}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    public String extractUsername(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            Object claim = getClaimsFromToken(token).get("userId");
            if (claim instanceof Number number) {
                return number.longValue();
            }
            if (claim instanceof String value) {
                return Long.parseLong(value);
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return getClaimsFromToken(token).get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from token: {}", e.getMessage());
            return null;
        }
    }

    public Boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
