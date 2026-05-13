package com.bazuuyu.b2b.core.security;

import com.bazuuyu.b2b.core.config.SecurityConstants;
import com.bazuuyu.b2b.core.entity.Role;
import com.bazuuyu.b2b.core.entity.User;
import com.bazuuyu.b2b.core.enums.UserAccountStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms:36000000}")
    private long expirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = Map.of(
                SecurityConstants.USER_ID_CLAIM, user.getId(),
                SecurityConstants.EMAIL_CLAIM, user.getEmail(),
                SecurityConstants.ROLE_CLAIM, user.getRole().name(),
                SecurityConstants.STATUS_CLAIM, user.getAccountStatus().name()
        );

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public AuthenticatedUser extractAuthenticatedUser(String token) {
        Claims claims = extractAllClaims(token);
        return new AuthenticatedUser(
                claims.get(SecurityConstants.USER_ID_CLAIM, Long.class),
                claims.getSubject(),
                claims.get(SecurityConstants.EMAIL_CLAIM, String.class),
                Role.valueOf(claims.get(SecurityConstants.ROLE_CLAIM, String.class)),
                UserAccountStatus.valueOf(claims.get(SecurityConstants.STATUS_CLAIM, String.class))
        );
    }

    public boolean validateToken(String token) {
        extractAllClaims(token);
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
