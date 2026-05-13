package com.bazuuyu.b2b.core.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${app.internal-secret:}")
    private String internalSecret;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        try {
            if (path.startsWith("/internal/")) {
                handleInternalRequest(request, response, chain);
                return;
            }

            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();

                if (!token.isEmpty()
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwtUtil.validateToken(token)) {
                        AuthenticatedUser user = jwtUtil.extractAuthenticatedUser(token);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        List.of(() -> user.role().asAuthority())
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }

            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
        } catch (IllegalArgumentException | JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid");
        }
    }

    private void handleInternalRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String internalHeader = request.getHeader("X-Internal-Secret");

        if (internalSecret == null || internalSecret.isBlank()
                || internalHeader == null
                || !internalHeader.equals(internalSecret)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized internal request");
            return;
        }

        UsernamePasswordAuthenticationToken internalAuth =
                new UsernamePasswordAuthenticationToken(
                        "internal-service",
                        null,
                        List.of(() -> "ROLE_INTERNAL")
                );

        try {
            SecurityContextHolder.getContext().setAuthentication(internalAuth);
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
