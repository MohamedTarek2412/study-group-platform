package com.studygroup.gateway.config;

import com.studygroup.gateway.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic route definitions for the API Gateway.
 *
 * Routes defined here take precedence over application.yml routes
 * (both sets are merged by Spring Cloud Gateway, but explicit beans
 * make the JWT filter wiring cleaner).
 *
 * Route strategy:
 *  • /api/auth/**        → auth-service        (NO JWT — login / register)
 *  • /api/users/**       → user-service         (JWT required)
 *  • /api/groups/**      → group-service        (JWT required)
 *  • /api/discussions/** → discussion-service   (JWT required)
 */
@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${AUTH_SERVICE_URL:http://auth-service:8081}")
    private String authServiceUrl;

    @Value("${USER_SERVICE_URL:http://user-service:8082}")
    private String userServiceUrl;

    @Value("${GROUP_SERVICE_URL:http://group-service:8083}")
    private String groupServiceUrl;

    @Value("${DISCUSSION_SERVICE_URL:http://discussion-service:8084}")
    private String discussionServiceUrl;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        JwtAuthFilter.Config jwtConfig = new JwtAuthFilter.Config();

        return builder.routes()

            // ── AUTH (public — no JWT) ─────────────────────────────
            .route("auth-service", r -> r
                .path("/api/auth/**")
                .uri(authServiceUrl))

            // ── USER SERVICE (protected) ───────────────────────────
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f.filter(jwtAuthFilter.apply(jwtConfig)))
                .uri(userServiceUrl))

            // ── GROUP SERVICE (protected) ──────────────────────────
            .route("group-service", r -> r
                .path("/api/groups/**")
                .filters(f -> f.filter(jwtAuthFilter.apply(jwtConfig)))
                .uri(groupServiceUrl))

            // ── DISCUSSION SERVICE (protected) ─────────────────────
            .route("discussion-service", r -> r
                .path("/api/discussions/**")
                .filters(f -> f.filter(jwtAuthFilter.apply(jwtConfig)))
                .uri(discussionServiceUrl))

            .build();
    }
}