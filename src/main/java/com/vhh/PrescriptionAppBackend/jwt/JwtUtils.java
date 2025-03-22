package com.vhh.PrescriptionAppBackend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vhh.PrescriptionAppBackend.exception.InvalidParamException;
import com.vhh.PrescriptionAppBackend.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private Long expiration;

    public String generateToken(User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        try {
            String token = Jwts.builder()
            .claims(claims)
            .subject(user.getEmail())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(this.getSignKey())
            .compact();
        return token;
        } catch (Exception e) {
            throw new InvalidParamException("Không thể tạo jwt token, lỗi: " + e.getMessage());
        }
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(this.getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}

