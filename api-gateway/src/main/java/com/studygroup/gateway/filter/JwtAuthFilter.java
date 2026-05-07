package com.studygroup.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Gateway filter that validates JWT tokens on protected routes.
 *
 * Usage in application.yml:
 *   filters:
 *     - JwtAuth
 *
 * On success  → forwards the request with X-User-Id and X-User-Email headers added.
 * On failure  → returns 401 Unauthorized immediately.
 */
@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // ── 1. Check header presence ───────────────────────
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or malformed Authorization header for path: {}",
                        exchange.getRequest().getPath());
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            // ── 2. Validate & parse token ──────────────────────
            try {
                SecretKey key = Keys.hmacShaKeyFor(
                        jwtSecret.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String userId    = claims.getSubject();
                String userEmail = claims.get("email", String.class);

                // ── 3. Forward user info as headers downstream ─
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r
                                .header("X-User-Id",    userId    != null ? userId    : "")
                                .header("X-User-Email", userEmail != null ? userEmail : ""))
                        .build();

                log.debug("JWT valid — userId={}, path={}",
                        userId, exchange.getRequest().getPath());

                return chain.filter(mutatedExchange);

            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                return unauthorized(exchange);
            }
        };
    }

    // ── Helpers ────────────────────────────────────────────────

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders()
                .add(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"studygroup\"");
        return exchange.getResponse().setComplete();
    }

    // Empty config class — no per-route config needed for this filter
    public static class Config {}
}