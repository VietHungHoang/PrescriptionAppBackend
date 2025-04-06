package com.vhh.PrescriptionAppBackend.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
   private String message;
   
   private String token;

   @JsonProperty("refresh_token")
   private String refreshToken;

   private String tokenType = "Bearer";

   // User detail
    private Long id;
    
    private String username;

    private List<String> roles;
}
