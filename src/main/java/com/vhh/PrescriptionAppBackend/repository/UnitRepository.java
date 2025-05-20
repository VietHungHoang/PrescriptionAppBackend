package com.vhh.PrescriptionAppBackend.repository;


import com.vhh.PrescriptionAppBackend.model.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    // Tìm kiếm đơn vị thuốc theo ID
    Optional<Unit> findById(Long id);
}
