package com.doritech.microservice.AuthenticationService.Filter;

import com.doritech.microservice.AuthenticationService.Service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final List<String> PUBLIC_URLS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/send-otp",
            "/auth/verify-otp",
            "/auth/update-password",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/health",
            "/actuator",
            "/actuator/health",
            "/error");

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Allow public URLs without token
        if (PUBLIC_URLS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "Authorization header missing or invalid");
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(jwt)) {
                sendError(response, 401, "Invalid or expired token");
                return;
            }

            Claims claims = jwtService.extractAllClaims(jwt);
            String loginId = claims.getSubject();
            String roleName = claims.get("roleName", String.class);

            if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginId,
                        null,
                        roleName != null
                                ? Collections.singletonList(new SimpleGrantedAuthority(roleName))
                                : Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendError(response, 403, "JWT processing failed: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(String.format(
                "{\"statusCode\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status,
                HttpStatus.valueOf(status).getReasonPhrase(),
                message));
    }
}