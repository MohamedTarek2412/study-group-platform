package com.studygroup.user.security.config;

import com.studygroup.user.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the user-service.
 *
 * <p>This service acts as a JWT <em>resource server</em> — it validates tokens
 * but never issues them (auth-service owns that responsibility).
 *
 * <p>RBAC rules:
 * <ul>
 *   <li>Public  → GET /api/v1/users/{username} (public profile browsing)</li>
 *   <li>STUDENT → GET /me, PATCH /me, POST /me/creator-application</li>
 *   <li>ADMIN   → GET /admin/**, PATCH /admin/**</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize on methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    private static final String[] PUBLIC_GET_PATTERNS = {
            "/api/v1/users/{username}",
            "/api/v1/users/search",
            "/actuator/health",
            "/actuator/info"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Stateless REST API — disable session & CSRF ──────────────
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── Route-level authorisation ────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                    // Public profile browsing & health
                    .requestMatchers(HttpMethod.GET, PUBLIC_GET_PATTERNS).permitAll()

                    // Any authenticated user can read their own profile
                    .requestMatchers(HttpMethod.GET,   "/api/v1/users/me").authenticated()
                    .requestMatchers(HttpMethod.PATCH,  "/api/v1/users/me").authenticated()

                    // Creator application: STUDENTs and CREATORs only
                    .requestMatchers(HttpMethod.POST,
                            "/api/v1/users/me/creator-application")
                            .hasAnyRole("STUDENT", "CREATOR")

                    // All /admin/** routes require ADMIN role
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                    // Everything else requires authentication
                    .anyRequest().authenticated()
            )

            // ── JWT filter before Spring's username/password filter ──────
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
