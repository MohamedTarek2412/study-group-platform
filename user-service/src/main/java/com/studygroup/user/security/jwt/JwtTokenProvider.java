package com.studygroup.user.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

/**
 * Utility for parsing and validating JWTs.
 *
 * <p>The user-service is a <em>consumer</em> of tokens issued by auth-service.
 * It never mints tokens. Validation uses the shared HMAC-SHA256 secret.
 *
 * <p>Single Responsibility: knows only how to parse / validate.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.secretKey  = Keys.hmacShaKeyFor(keyBytes);
    }

    // ─── Extraction ───────────────────────────────────────────────────────

    public UUID extractAuthUserId(String token) {
        return UUID.fromString(
                parseClaims(token).getSubject()
        );
    }

    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    public String extractUsername(String token) {
        return (String) parseClaims(token).get("username");
    }

    // ─── Validation ───────────────────────────────────────────────────────

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string empty: {}", e.getMessage());
        }
        return false;
    }

    // ─── Internals ────────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
