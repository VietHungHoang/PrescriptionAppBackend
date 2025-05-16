package com.vhh.PrescriptionAppBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vhh.PrescriptionAppBackend.model.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
}
