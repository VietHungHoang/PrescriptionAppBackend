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

import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.response.DosageResponse;
import com.vhh.PrescriptionAppBackend.model.response.DrugInPrescriptionResponse;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponse;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleResponse;
import com.vhh.PrescriptionAppBackend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    @Override
    public Prescription save(Prescription prescription) {
        return this.prescriptionRepository.save(prescription);
    }
    
    public List<PrescriptionResponse> getPrescriptionsByUserIdAndStatus(Long userId, int status) {
        List<Prescription> prescriptions = prescriptionRepository.findByUserIdAndStatus(userId, status);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setName(prescription.getName());
        response.setHospital(prescription.getHospital());
        response.setDoctorName(prescription.getDoctorName());
        response.setConsultationDate(prescription.getConsultationDate());
        response.setFollowUpDate(prescription.getFollowUpDate());
        response.setStatus(prescription.getStatus());

        List<DrugInPrescriptionResponse> drugs = prescription.getDrugInPrescriptions().stream()
                .map(dip -> {
                    DrugInPrescriptionResponse dipResp = new DrugInPrescriptionResponse();
                    dipResp.setDrugName(dip.getDrug().getName());
                    dipResp.setUnitName(dip.getUnit().getName());

                    dipResp.setDosages(dip.getTimeDosages().stream()
                            .map(dosage -> new DosageResponse(dosage.getDosage()))
                            .collect(Collectors.toList()));

                    dipResp.setSchedules(
                            dip.getSchedules().stream()
                                    .map(schedule -> {
                                        ScheduleResponse scheduleResp = new ScheduleResponse();
                                        scheduleResp.setDate(schedule.getDate().toString());
                                        scheduleResp.setTimeDosages(null);
                                        return scheduleResp;
                                    })
                                    .collect(Collectors.toList())
                    );

                    return dipResp;
                })
                .collect(Collectors.toList());

        response.setDrugs(drugs);
        return response;
    }

}
