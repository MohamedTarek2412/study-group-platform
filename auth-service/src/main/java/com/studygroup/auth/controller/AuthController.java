package com.studygroup.auth.controller;

import com.studygroup.auth.dto.AuthResponse;
import com.studygroup.auth.dto.LoginRequest;
import com.studygroup.auth.dto.RegisterRequest;
import com.studygroup.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> me(Authentication authentication) {
        return ResponseEntity.ok("Authenticated as: " + authentication.getName());
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminPing() {
        return ResponseEntity.ok("Admin endpoint is accessible");
    }

    @GetMapping("/public/health")
    public ResponseEntity<String> publicHealth() {
        return ResponseEntity.ok("Auth service is up");
    }
}
