package com.vhh.PrescriptionAppBackend.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleLoginRequest {
    @NotBlank(message = "idToken cannot blank")
    private String idToken;
}
