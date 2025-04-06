package com.vhh.PrescriptionAppBackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoogleAuthResponse {

    @JsonProperty("google_account_id")
    private String googleAccountId;

    private String email;

    private String name;

    private String status;
    private String token;

    // Static factory methods for convenience
    public static GoogleAuthResponse loginSuccess(String token) {
        GoogleAuthResponse res = new GoogleAuthResponse();
        res.setStatus("LOGIN_SUCCESS");
        res.setToken(token);
        return res;
    }

    public static GoogleAuthResponse registrationRequired(String email, String name, String googleId) {
        GoogleAuthResponse res = new GoogleAuthResponse();
        res.setStatus("REGISTRATION_REQUIRED");
        res.setEmail(email);
        res.setName(name);
        res.setGoogleAccountId(googleId);
        return res;
    }
}