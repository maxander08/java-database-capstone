package com.project.back_end.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * Service layer responsible for issuing and verifying secure session tokens
 * to maintain authorized session access for Admins, Doctors, and Patients.
 */
public class TokenService {

    // Simple in-memory storage simulating a token repository cache
    private static final Map<String, TokenMetadata> tokenCache = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    private static class TokenMetadata {
        String email;
        String role;
        LocalDateTime expirationTime;

        TokenMetadata(String email, String role, LocalDateTime expirationTime) {
            this.email = email;
            this.role = role;
            this.expirationTime = expirationTime;
        }
    }

    /**
     * Generates a unique, cryptographically secure string token for a authenticated user session.
     * Tokens are configured to automatically expire 60 minutes from creation.
     */
    public String generateSessionToken(String email, String role) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token = base64Encoder.encodeToString(randomBytes);

        // Sessions expire after 1 hour
        LocalDateTime expiration = LocalDateTime.now().plusHours(1);
        tokenCache.put(token, new TokenMetadata(email, role, expiration));

        return token;
    }

    /**
     * Validates if a token is present, active, and belongs to the expected access role.
     */
    public boolean isTokenValidForRole(String token, String expectedRole) {
        TokenMetadata metadata = tokenCache.get(token);

        if (metadata == null) {
            System.err.println("Authentication Error: Invalid access token.");
            return false;
        }

        if (LocalDateTime.now().isAfter(metadata.expirationTime)) {
            System.err.println("Authentication Error: Token session has expired.");
            tokenCache.remove(token); // Clear expired entry
            return false;
        }

        if (!metadata.role.equalsIgnoreCase(expectedRole)) {
            System.err.println("Authorization Error: Insufficient permissions for role: " + expectedRole);
            return false;
        }

        return true;
    }

    /**
     * Explicitly invalidates a token session upon user logout.
     */
    public void invalidateToken(String token) {
        tokenCache.remove(token);
    }
}
