package com.jean.quizzer.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jean.quizzer.models.User;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Generates a JWT token for the specified user.
     *
     * @param user the user for whom the token is to be generated
     * @return a JWT token as a String
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Decodes the given JWT token and retrieves the subject (email) from it.
     *
     * @param token the JWT token to decode
     * @return the subject (email) contained in the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public User decodeToken(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new User(claims.get("id", Long.class), claims.get("email", String.class));
    }

    /**
     * Checks if the given JWT token is close to expiry.
     *
     * This method determines if the expiration date of the token is within the next
     * 5 minutes.
     *
     * @param token the JWT token to check for expiry
     * @return true if the token is close to expiry, false otherwise
     */
    public boolean isCloseToExpiry(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date(System.currentTimeMillis() + 1000 * 60 * 5));
    }

    /**
     * Validates the given JWT token by checking its expiration date.
     *
     * This method parses the token and retrieves the expiration date,
     * then checks if the token is still valid (i.e., not expired).
     *
     * @param token the JWT token to validate
     * @return true if the token is valid (not expired), false otherwise
     */
    public boolean isValidToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .after(new Date(System.currentTimeMillis()));
    }

    /**
     * Retrieves the signing key used for JWT token generation and validation.
     *
     * @return a SecretKey object representing the signing key
     * @throws IllegalStateException if the JWT secret key is not configured
     */
    private SecretKey getSigningKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
