package com.vhh.PrescriptionAppBackend.model.entity;

import jakarta.persistence.Entity;

@Entity
public class Doctor extends User{
    private String speciality;
    private String experienceLevel;
    private String addressOfPrescription;
}
