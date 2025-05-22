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
import com.vhh.PrescriptionAppBackend.model.response.DosageResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.DrugInPrescriptionResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleResponseKiet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService implements IPrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    public List<PrescriptionResponseKiet> getPrescriptionsByUserIdAndStatus(Long userId, int status) {
        List<Prescription> prescriptions = prescriptionRepository.findByUserIdAndStatus(userId, status);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrescriptionResponseKiet mapToResponse(Prescription prescription) {
        PrescriptionResponseKiet response = new PrescriptionResponseKiet();
        response.setId(prescription.getId());
        response.setName(prescription.getName());
        response.setHospital(prescription.getHospital());
        response.setDoctorName(prescription.getDoctorName());
        response.setConsultationDate(prescription.getConsultationDate());
        response.setFollowUpDate(prescription.getFollowUpDate());
        response.setStatus(prescription.getStatus());

        List<DrugInPrescriptionResponseKiet> drugs = prescription.getDrugInPrescriptions().stream()
                .map(dip -> {
                    DrugInPrescriptionResponseKiet dipResp = new DrugInPrescriptionResponseKiet();
                    dipResp.setDrugName(dip.getDrug().getName());
                    dipResp.setUnitName(dip.getUnit().getName());

                    // dipResp.setDosages(dip.getTimeDosages().stream()
                    //         .map(dosage -> new DosageResponseKiet(dosage.getDosage()))
                    //         .collect(Collectors.toList()));

                    dipResp.setSchedules(
                            dip.getSchedules().stream()
                                    .map(schedule -> {
                                        ScheduleResponseKiet scheduleResp = new ScheduleResponseKiet();
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

    @Override
    public Prescription save(Prescription prescription) {
        return prescriptionRepository.save(prescription);
        
    }

}
