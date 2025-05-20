package com.vhh.PrescriptionAppBackend.repository;

import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByUserIdAndStatus(Long userId, Integer status);
}
