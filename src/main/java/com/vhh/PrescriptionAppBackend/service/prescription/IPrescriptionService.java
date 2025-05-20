package com.vhh.PrescriptionAppBackend.service.prescription;

import com.vhh.PrescriptionAppBackend.model.entity.Prescription;

public interface IPrescriptionService {
    Prescription save(Prescription prescription);
}