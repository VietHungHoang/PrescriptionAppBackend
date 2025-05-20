package com.vhh.PrescriptionAppBackend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vhh.PrescriptionAppBackend.model.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

}