package com.example.society.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.time.Duration;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT token with username and role claims.
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Overloaded generateToken method with default role "USER".
     */
    public String generateToken(String username) {
        return generateToken(username, "USER");
    }

    /**
     * Extract username (subject) from JWT token.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract role claim from JWT token.
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // Throws if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to extract all claims from JWT token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



/**
 * Generate a short-lived JWT with a custom TTL and a token type discriminator.
 * Example: subject="QR_ENTRY", typ="QR"
 */
public String generateTokenWithCustomTTL(String subject, Duration ttl) {
    if (ttl == null || ttl.isNegative() || ttl.isZero()) {
        throw new IllegalArgumentException("TTL must be positive");
    }
    long now = System.currentTimeMillis();
    Date iat = new Date(now);
    Date exp = new Date(now + ttl.toMillis());

    return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim("typ", "QR") // discriminator to separate from auth tokens
            .signWith(getSigningKey())
            .compact();
}

/**
 * Optionally enforce token type via 'typ' claim for specific flows (e.g., QR).
 */
public boolean validateTokenType(String token, String expectedTyp) {
    try {
        Claims claims = extractAllClaims(token);
        String typ = claims.get("typ", String.class);
        return expectedTyp == null || expectedTyp.equals(typ);
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}

    /**
     * Extract roles from JWT token (supports multiple roles in a claim).
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");

        if (roles instanceof String roleStr) {
            return List.of(roleStr); // single role stored as string
        } else if (roles instanceof java.util.List<?> roleList) {
            return roleList.stream().map(Object::toString).toList();
        }
        return List.of();
    }


}
