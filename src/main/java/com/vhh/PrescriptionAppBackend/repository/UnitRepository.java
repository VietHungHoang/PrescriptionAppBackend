package com.vhh.PrescriptionAppBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vhh.PrescriptionAppBackend.model.entity.Unit;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    // Tìm kiếm đơn vị thuốc theo ID
    Optional<Unit> findById(Long id);
}
