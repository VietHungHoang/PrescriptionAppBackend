package com.vhh.PrescriptionAppBackend.service.token;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleTokenVerifierService.class);

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService(@Value("${google.clientId}") String googleClientId) {
         HttpTransport transport = new NetHttpTransport();
         JsonFactory jsonFactory = new GsonFactory();
         this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
         logger.info("Google Token Verifier initialized with Client ID: {}", googleClientId);
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = this.verifier.verify(idTokenString);
            if (idToken != null) {
                logger.info("Google ID Token verified successfully.");
                return idToken.getPayload();
            } else {
                logger.warn("Invalid Google ID token received (verifier returned null).");
                return null;
            }
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Error verifying Google ID token: {}", e.getMessage(), e);
            return null;
        } catch (IllegalArgumentException e){
             logger.error("Error verifying Google ID token - Likely malformed token string: {}", e.getMessage());
             return null;
        }
    }

    // Bạn NÊN thêm phương thức kiểm tra Nonce ở đây nếu client gửi nonce
    public boolean verifyNonce(GoogleIdToken.Payload payload, String expectedNonce) {
         if (payload == null || expectedNonce == null) {
             return false;
         }
         String tokenNonce = (String) payload.get("nonce");
         boolean matches = expectedNonce.equals(tokenNonce);
         if (!matches) {
             logger.warn("Nonce mismatch! Expected: {}, Got: {}", expectedNonce, tokenNonce);
         } else {
              logger.info("Nonce verified successfully.");
         }
         return matches;
     }
}