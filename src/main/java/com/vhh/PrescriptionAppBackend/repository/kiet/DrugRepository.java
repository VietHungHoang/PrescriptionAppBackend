package com.vhh.PrescriptionAppBackend.repository.kiet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponseKiet;

public interface DrugRepository extends JpaRepository<Drug, Long> {

    @Query("SELECT new com.vhh.PrescriptionAppBackend.model.response.DrugNameResponseKiet(d.id, d.name) FROM Drug d")
    List<DrugNameResponseKiet> findAllSimple();
}
