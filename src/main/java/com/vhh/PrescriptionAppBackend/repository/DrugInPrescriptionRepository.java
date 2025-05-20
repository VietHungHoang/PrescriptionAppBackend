package com.vhh.PrescriptionAppBackend.repository;

import com.vhh.PrescriptionAppBackend.model.entity.DrugInPrescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugInPrescriptionRepository extends JpaRepository<DrugInPrescription, Long> {
}
