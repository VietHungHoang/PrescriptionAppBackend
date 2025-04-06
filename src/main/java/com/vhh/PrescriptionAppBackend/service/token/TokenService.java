package com.vhh.PrescriptionAppBackend.service.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vhh.PrescriptionAppBackend.exception.DataNotFoundException;
import com.vhh.PrescriptionAppBackend.exception.ExpiredTokenException;
import com.vhh.PrescriptionAppBackend.jwt.JwtUtils;
import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.repository.TokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private static final int MAX_TOKENS = 3;

    @Value("${jwt.expirationMs}")
    private int expirationMs;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final TokenRepository tokenRepository;

    private final JwtUtils jwtUtils;

    @Transactional
    @Override
    public Token addToken(User user, String token) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();

        if (tokenCount > TokenService.MAX_TOKENS) {
            // Updating
        }
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(this.expirationMs);
        LocalDateTime expirationRefreshTokenDate = LocalDateTime.now().plusSeconds(this.expirationRefreshToken);

        // Create new Token object
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDate)
                .refreshToken(UUID.randomUUID().toString())
                .refreshExpirationDate(expirationRefreshTokenDate)
                .build();

        tokenRepository.save(newToken);
        return newToken;
    }

    @Override
    public Token refreshToken(User user, String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if (existingToken == null) {
            throw new DataNotFoundException("Refresh token not found");
        }

        if (existingToken.getRefreshExpirationDate().compareTo(LocalDateTime.now()) < 0) {
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token has expired");
        }

        String token = jwtUtils.generateToken(user);
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(this.expirationMs);
        LocalDateTime expirationRefreshTokenDate = LocalDateTime.now().plusSeconds(this.expirationRefreshToken);
        existingToken = Token.builder()
                .token(token)
                .refreshToken(UUID.randomUUID().toString())
                .expirationDate(expirationDate)
                .refreshExpirationDate(expirationRefreshTokenDate)
                .build();
        return existingToken;
    }

}
