package com.vhh.PrescriptionAppBackend.model.entity;

public class Doctor extends User {
    public Doctor(String googleAccountId, String email, String name, String photoUrl) {
        super(googleAccountId, email, name, photoUrl);
    }
}
