package com.vhh.PrescriptionAppBackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private String googleAccountId;
    private String email;
    private String name;
    private String photoUrl;
}
