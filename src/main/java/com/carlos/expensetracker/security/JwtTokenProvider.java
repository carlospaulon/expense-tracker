package com.carlos.expensetracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;

    @Getter
    private final long jwtExpiresMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiresMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiresMs = jwtExpiresMs;
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiresMs);

        return buildToken(userDetails, now, expiryDate);
    }

    public long getExpiresInSeconds() {
        return jwtExpiresMs / 1000;
    }

    //generateTokenFromUserId i took it off

    public UUID getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);

        return UUID.fromString(claims.getSubject());
    }


    public String getEmailFromToken(String token) {
        Claims claims = extractClaims(token);

        return claims.get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException |
                 ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException ex) {

            log.warn("Invalid JWT Token: {}", ex.getMessage());
            return false;
        }

    }

    //Inline return in both or try to change the method for use on both
    //To let more usable, i could change the userDetails for every asked attribute
    private String buildToken(CustomUserDetails userDetails, Date now, Date expiryDate) {
        return Jwts.builder()
                .subject(userDetails.getUserId().toString())
                .claim("email", userDetails.getEmail())
                .claim("username", userDetails.getUsernameField())
                .claim("role", userDetails.getUser().getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
