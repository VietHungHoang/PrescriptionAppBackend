package com.vhh.PrescriptionAppBackend.service.prescription;

import java.util.List;

import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponseKiet;

public interface IPrescriptionService {
    Prescription save(Prescription prescription);
    List<PrescriptionResponseKiet> getPrescriptionsByUserIdAndStatus(Long userId, int status);
}