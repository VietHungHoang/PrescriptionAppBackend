package com.vhh.PrescriptionAppBackend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;

public interface DrugRepository extends JpaRepository<Drug, Long> {

    @Query("SELECT new com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse(d.id, d.name) FROM Drug d")
    List<DrugNameResponse> findAllSimple();

    List<Drug> findByNameContainingIgnoreCase(String name);
}