package com.appointment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkeymysecretkey";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // =========================
    // GENERATE USER TOKEN
    // =========================

    public String generateToken(String email) {

        return generateToken(email, "USER");
    }


    // =========================
    // GENERATE TOKEN WITH ROLE
    // =========================

    public String generateToken(
            String email,
            String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 24 * 60 * 60 * 1000
                        )
                )
                .signWith(key)
                .compact();
    }


    // =========================
    // EXTRACT EMAIL
    // =========================

    public String extractEmail(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }


    // =========================
    // EXTRACT ROLE
    // =========================

    public String extractRole(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }


    // =========================
    // VALIDATE TOKEN
    // =========================

    public boolean isTokenValid(
            String token,
            String email) {

        return extractEmail(token).equals(email);
    }
}