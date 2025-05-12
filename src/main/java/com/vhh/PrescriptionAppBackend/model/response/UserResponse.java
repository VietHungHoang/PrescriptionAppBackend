package com.vhh.PrescriptionAppBackend.model.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhh.PrescriptionAppBackend.model.entity.Role;
import com.vhh.PrescriptionAppBackend.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("height")
    private Double height;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .weight(user.getUserSetting() != null ? user.getUserSetting().getWeight() : null)
                .height(user.getUserSetting() != null ? user.getUserSetting().getHeight() : null)
                .build();
    }
}