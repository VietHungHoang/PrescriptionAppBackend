package com.vhh.PrescriptionAppBackend.model.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerRegisterGoogleRequest {
    @NotBlank(message = "Google account id is required")
    @JsonProperty("google_account_id")
    private String googleAccountId;

    @NotBlank(message = "First name is required")
    private String name;

    @NotBlank(message = "First name is required")
    @Email
    private String email;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("country_id")
    private Long countryId;

    private LocalDate dob;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String gender;
}
