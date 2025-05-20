package com.vhh.PrescriptionAppBackend.service.prescription;

import java.util.List;

import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponse;

public interface IPrescriptionService {
    Prescription save(Prescription prescription);
    List<PrescriptionResponse> getPrescriptionsByUserIdAndStatus(Long userId, int status);
}