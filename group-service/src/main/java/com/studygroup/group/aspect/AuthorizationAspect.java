package com.studygroup.group.aspect;

import com.studygroup.group.exception.UnauthorizedException;
import com.studygroup.group.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {

    private final JwtUtil jwtUtil;

    @Before("@annotation(com.studygroup.group.annotation.RequireAuth)")
    public void checkAuthentication(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        String token = extractTokenFromRequest(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Authentication required");
        }
    }

    @Before("@annotation(com.studygroup.group.annotation.RequireRole('ADMIN'))")
    public void checkAdminRole(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        String token = extractTokenFromRequest(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Authentication required");
        }

        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equals(role)) {
            throw new UnauthorizedException("Admin role required");
        }
    }

    @Before("@annotation(com.studygroup.group.annotation.RequireRole('CREATOR'))")
    public void checkCreatorRole(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        String token = extractTokenFromRequest(request);

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Authentication required");
        }

        String role = jwtUtil.extractRole(token);
        if (!"CREATOR".equals(role) && !"ADMIN".equals(role)) {
            throw new UnauthorizedException("Creator role required");
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedException("Could not extract request context");
        }
        return attributes.getRequest();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
