package com.vhh.PrescriptionAppBackend.service.prescription;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;
import com.vhh.PrescriptionAppBackend.repository.DrugRepository;
import com.vhh.PrescriptionAppBackend.repository.PrescriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    @Override
    public Prescription save(Prescription prescription) {
        return this.prescriptionRepository.save(prescription);
    }
    
}