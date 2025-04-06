package com.vhh.PrescriptionAppBackend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vhh.PrescriptionAppBackend.exception.InvalidParamException;
import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.repository.TokenRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private Long expiration;

    private final TokenRepository tokenRepository;

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
        String email = extractClaim(token, Claims::getSubject);
        return email;
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public boolean validateToken(String token, User userDetails) {
        try {
            String email = this.extractEmail(token);
            Token existingToken = tokenRepository.findByToken(token);
            if (existingToken == null || existingToken.isRevoked() == true) {
                return false;
            }
            return (email.equals(userDetails.getUsername())) && !this.isTokenExpired(token);
        } catch (MalformedJwtException e) {
            System.err.println("Jwt token không hợp lệ" + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Jwt token đã hết hạn" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Jwt token không hỗ trợ" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Jwt claims string is empty" + e.getMessage());
        }
        return false;
    }
}
