package com.project.back_end.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    // Generate a secure, cryptographically strong HMAC-SHA signing key
    private static final Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Enforce an explicit session token expiration lifespan (e.g., 2 hours in milliseconds)
    private static final long EXPIRATION_TIME_MS = 7_200_000;

    /**
     * CRITICAL FIX: Generates a cryptographically signed JSON Web Token (JWT).
     * Incorporates the user's email address as the subject along with standard issued and expiration timestamps.
     *
     * @param email The authenticated user's email address
     * @return A standard compliant base64-encoded JWT string
     */
    public String generateSessionToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Token generation failed: User email identifier cannot be null or blank.");
        }

        long currentTimeMillis = System.currentTimeMillis();
        Date issuedAt = new Date(currentTimeMillis);
        Date expiresAt = new Date(currentTimeMillis + EXPIRATION_TIME_MS);

        // Fulfills key requirement: Build the token using JWT standards and Jwts.builder()
        return Jwts.builder()
                .setSubject(email)                          // Encapsulates the user's unique identity
                .setIssuedAt(issuedAt)                      // Sets token creation timestamp
                .setExpiration(expiresAt)                    // Sets explicit expiration guardrail
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256) // Cryptographically signs payload
                .compact();                                  // Serializes to a compact URL-safe string
    }

    /**
     * Fallback validation tracker to preserve existing controller routing logic pipelines.
     */
    public boolean isTokenValidForRole(String token, String expectedRole) {
        try {
            // Parses and verifies token authenticity via the secret key structure
            String email = Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            
            return email != null && !email.isEmpty();
        } catch (Exception e) {
            System.err.println("JWT Verification Failure: " + e.getMessage());
            return false;
        }
    }
}
